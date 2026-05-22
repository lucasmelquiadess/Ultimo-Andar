import type {
  AddendumRequest,
  AuditEvent,
  ContractRequest,
  CurrentUser,
  Dashboard,
  DocumentRecord,
  LeaseContract,
  Owner,
  PropertyPhoto,
  RentalProperty,
  TemplateRecord,
  Tenant,
  TerminationRequest,
  UserRecord,
  UserRequest
} from '../types/domain';

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? '';
const TOKEN_KEY = 'ultimo-andar.auth-token';
const TOKEN_EXPIRES_KEY = 'ultimo-andar.auth-token-expires-at';

let authToken = localStorage.getItem(TOKEN_KEY) ?? '';
let authTokenExpiresAt = Number(localStorage.getItem(TOKEN_EXPIRES_KEY) ?? '0');

type LoginPayload = {
  token: string;
  expiresAt: number;
  user: CurrentUser;
};

type ApiErrorBody = {
  message?: string;
  details?: string[];
};

function tokenIsValid() {
  return Boolean(authToken) && authTokenExpiresAt * 1000 > Date.now() + 5000;
}

function authHeaders(headers: HeadersInit = {}) {
  const result = new Headers(headers);
  if (tokenIsValid()) {
    result.set('Authorization', `Bearer ${authToken}`);
  }
  return result;
}

function setAuthSession(token: string, expiresAt: number) {
  authToken = token;
  authTokenExpiresAt = expiresAt;
  localStorage.setItem(TOKEN_KEY, token);
  localStorage.setItem(TOKEN_EXPIRES_KEY, String(expiresAt));
}

function clearAuthToken() {
  authToken = '';
  authTokenExpiresAt = 0;
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(TOKEN_EXPIRES_KEY);
}

async function responseError(response: Response, fallbackMessage: string) {
  if (response.status === 401) {
    clearAuthToken();
    return new Error('Sessão expirada ou credenciais inválidas.');
  }
  if (response.status === 403) {
    return new Error('Seu usuário não tem permissão para esta operação.');
  }

  const error = (await response.json().catch(() => null)) as ApiErrorBody | null;
  const details = error?.details?.length ? ` ${error.details.join(' ')}` : '';
  return new Error(`${error?.message ?? fallbackMessage}${details}`);
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = authHeaders(options.headers);
  headers.set('Content-Type', 'application/json');
  const response = await fetch(`${API_BASE}/api${path}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    throw await responseError(response, 'Não foi possível concluir a operação.');
  }

  if (response.status === 204) {
    return undefined as T;
  }
  return response.json();
}

async function upload<T>(path: string, formData: FormData): Promise<T> {
  const response = await fetch(`${API_BASE}/api${path}`, {
    method: 'POST',
    headers: authHeaders(),
    body: formData
  });
  if (!response.ok) {
    throw await responseError(response, 'Não foi possível enviar o arquivo.');
  }
  return response.json();
}

async function documentBlob(id: string) {
  const response = await fetch(`${API_BASE}/api/documents/${id}/download`, {
    headers: authHeaders()
  });
  if (!response.ok) {
    throw await responseError(response, 'Não foi possível abrir o documento.');
  }
  const blob = await response.blob();
  const disposition = response.headers.get('Content-Disposition') ?? '';
  const match = disposition.match(/filename="?([^"]+)"?/i);
  return {
    blob,
    fileName: match?.[1] ?? 'documento.pdf'
  };
}

const query = (params: Record<string, string | undefined>) => {
  const search = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value) search.set(key, value);
  });
  const text = search.toString();
  return text ? `?${text}` : '';
};

export const api = {
  hasToken: () => {
    if (!tokenIsValid()) {
      clearAuthToken();
      return false;
    }
    return true;
  },
  login: async (username: string, password: string) => {
    const response = await request<LoginPayload>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password })
    });
    setAuthSession(response.token, response.expiresAt);
    return response.user;
  },
  logout: () => clearAuthToken(),
  me: () => request<CurrentUser>('/auth/me'),
  changePassword: (currentPassword: string, newPassword: string) => request<CurrentUser>('/auth/change-password', {
    method: 'POST',
    body: JSON.stringify({ currentPassword, newPassword })
  }),
  dashboard: () => request<Dashboard>('/dashboard'),
  owners: (search = '') => request<Owner[]>(`/owners${query({ search })}`),
  saveOwner: (data: Owner) => request<Owner>(data.id ? `/owners/${data.id}` : '/owners', { method: data.id ? 'PUT' : 'POST', body: JSON.stringify(data) }),
  deleteOwner: (id: string) => request<void>(`/owners/${id}`, { method: 'DELETE' }),
  tenants: (search = '') => request<Tenant[]>(`/tenants${query({ search })}`),
  saveTenant: (data: Tenant) => request<Tenant>(data.id ? `/tenants/${data.id}` : '/tenants', { method: data.id ? 'PUT' : 'POST', body: JSON.stringify(data) }),
  deleteTenant: (id: string) => request<void>(`/tenants/${id}`, { method: 'DELETE' }),
  properties: (search = '') => request<RentalProperty[]>(`/properties${query({ search })}`),
  saveProperty: (data: RentalProperty) => request<RentalProperty>(data.id ? `/properties/${data.id}` : '/properties', { method: data.id ? 'PUT' : 'POST', body: JSON.stringify(data) }),
  deleteProperty: (id: string) => request<void>(`/properties/${id}`, { method: 'DELETE' }),
  uploadPropertyPhoto: (id: string, file: File) => {
    const form = new FormData();
    form.append('file', file);
    return upload<PropertyPhoto>(`/properties/${id}/photos`, form);
  },
  contracts: (search = '') => request<LeaseContract[]>(`/contracts${query({ search })}`),
  generateContract: (data: ContractRequest) => request<LeaseContract>('/contracts/generate', { method: 'POST', body: JSON.stringify(data) }),
  reissueContract: (id: string) => request<LeaseContract>(`/contracts/${id}/reissue`, { method: 'POST' }),
  documents: (search = '') => request<DocumentRecord[]>(`/documents${query({ search })}`),
  addendums: (contractId?: string) => request(`/addendums${query({ contractId })}`),
  generateAddendum: (data: AddendumRequest) => request('/addendums/generate', { method: 'POST', body: JSON.stringify(data) }),
  generateTermination: (data: TerminationRequest) => request('/terminations/generate', { method: 'POST', body: JSON.stringify(data) }),
  templates: () => request<TemplateRecord[]>('/templates'),
  saveTemplate: (data: TemplateRecord) => request<TemplateRecord>('/templates', { method: 'POST', body: JSON.stringify(data) }),
  users: () => request<UserRecord[]>('/users'),
  createUser: (data: UserRequest) => request<UserRecord>('/users', { method: 'POST', body: JSON.stringify(data) }),
  resetUserPassword: (id: string, newPassword: string) => request<UserRecord>(`/users/${id}/reset-password`, { method: 'POST', body: JSON.stringify({ newPassword }) }),
  deactivateUser: (id: string) => request<UserRecord>(`/users/${id}`, { method: 'DELETE' }),
  audit: (limit = 50, filters: { actor?: string; action?: string; resourceType?: string } = {}) => request<AuditEvent[]>(`/audit${query({ limit: String(limit), ...filters })}`),
  openDocument: async (id: string) => {
    const { blob } = await documentBlob(id);
    const url = URL.createObjectURL(blob);
    window.open(url, '_blank', 'noopener,noreferrer');
    setTimeout(() => URL.revokeObjectURL(url), 60000);
  },
  downloadDocument: async (id: string) => {
    const { blob, fileName } = await documentBlob(id);
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = fileName;
    link.click();
    URL.revokeObjectURL(url);
  },
  documentUrl: (id: string) => `${API_BASE}/api/documents/${id}/download`
};
