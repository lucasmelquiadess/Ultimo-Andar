import { KeyRound, Plus, Save, Search, Trash2 } from 'lucide-react';
import { FormEvent, useEffect, useState } from 'react';
import { Notice } from '../components/Notice';
import { api } from '../services/api';
import type { AuditEvent, DocumentType, TemplateRecord, UserRecord, UserRequest, UserRole } from '../types/domain';
import { date, labels } from '../utils/formatters';

const initial: TemplateRecord = {
  documentType: 'LEASE_CONTRACT',
  name: '',
  content: '',
  active: true
};

const initialUser: UserRequest = {
  username: '',
  displayName: '',
  password: '',
  role: 'OPERATOR',
  active: true
};

const roles: UserRole[] = ['ADMIN', 'OPERATOR', 'READER'];
const roleLabels: Record<UserRole, string> = {
  ADMIN: 'Administrador',
  OPERATOR: 'Operador',
  READER: 'Leitura'
};

export function SettingsPage() {
  const [templates, setTemplates] = useState<TemplateRecord[]>([]);
  const [users, setUsers] = useState<UserRecord[]>([]);
  const [audit, setAudit] = useState<AuditEvent[]>([]);
  const [form, setForm] = useState<TemplateRecord>(initial);
  const [userForm, setUserForm] = useState<UserRequest>(initialUser);
  const [resetTarget, setResetTarget] = useState<UserRecord | null>(null);
  const [temporaryPassword, setTemporaryPassword] = useState('');
  const [auditFilters, setAuditFilters] = useState({ actor: '', action: '', resourceType: '' });
  const [notice, setNotice] = useState('');
  const [error, setError] = useState('');

  const loadTemplates = () => api.templates().then(setTemplates).catch((err) => setError(err.message));
  const loadSecurity = () => Promise.all([api.users(), api.audit(30, auditFilters)])
    .then(([userList, auditList]) => {
      setUsers(userList);
      setAudit(auditList);
    })
    .catch((err) => setError(err.message));

  useEffect(() => {
    void loadTemplates();
    void loadSecurity();
  }, []);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setError('');
    try {
      await api.saveTemplate(form);
      setNotice('Modelo salvo e ativado para próximas gerações.');
      setForm(initial);
      void loadTemplates();
      void loadSecurity();
    } catch (err) {
      setError((err as Error).message);
    }
  };

  const createUser = async (event: FormEvent) => {
    event.preventDefault();
    setError('');
    try {
      await api.createUser(userForm);
      setNotice('Usuário criado com sucesso.');
      setUserForm(initialUser);
      void loadSecurity();
    } catch (err) {
      setError((err as Error).message);
    }
  };

  const deactivateUser = async (id: string) => {
    if (!confirm('Deseja inativar este usuário?')) return;
    setError('');
    try {
      await api.deactivateUser(id);
      setNotice('Usuário inativado.');
      void loadSecurity();
    } catch (err) {
      setError((err as Error).message);
    }
  };

  const resetPassword = async (event: FormEvent) => {
    event.preventDefault();
    if (!resetTarget) return;
    setError('');
    try {
      await api.resetUserPassword(resetTarget.id, temporaryPassword);
      setNotice('Senha temporária definida. O usuário deverá trocá-la no próximo login.');
      setResetTarget(null);
      setTemporaryPassword('');
      void loadSecurity();
    } catch (err) {
      setError((err as Error).message);
    }
  };

  return (
    <div className="stack">
      <Notice message={notice} type="success" />
      <Notice message={error} type="error" />

      <div className="two-column">
        <section className="panel">
          <div className="section-title"><h2>Modelos cadastrados</h2></div>
          <div className="list">
            {templates.map((template) => (
              <div className="list-row" key={template.id}>
                <div>
                  <strong>{template.name}</strong>
                  <span>{labels[template.documentType]} {template.active ? 'ativo' : 'inativo'}</span>
                </div>
              </div>
            ))}
            {templates.length === 0 && <p className="empty">Usando os modelos HTML padrão derivados dos PDFs anexados.</p>}
          </div>
        </section>

        <section className="panel">
          <div className="section-title"><h2>Novo modelo HTML</h2></div>
          <form className="form-grid" onSubmit={submit}>
            <label>Tipo
              <select value={form.documentType} onChange={(event) => setForm({ ...form, documentType: event.target.value as DocumentType })}>
                {(['LEASE_CONTRACT', 'ADDENDUM', 'TERMINATION'] as DocumentType[]).map((type) => <option key={type} value={type}>{labels[type]}</option>)}
              </select>
            </label>
            <label>Nome do modelo
              <input required value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} />
            </label>
            <label className="full">Conteúdo HTML com placeholders
              <textarea className="template-editor" required value={form.content} onChange={(event) => setForm({ ...form, content: event.target.value })} />
            </label>
            <label className="checkbox-label">
              <input type="checkbox" checked={form.active} onChange={(event) => setForm({ ...form, active: event.target.checked })} />
              Ativar modelo imediatamente
            </label>
            <div className="form-actions full">
              <button type="submit"><Save size={17} /> Salvar modelo</button>
            </div>
          </form>
        </section>
      </div>

      <div className="two-column">
        <section className="panel">
          <div className="section-title"><h2>Usuários</h2></div>
          <div className="list">
            {users.map((user) => (
              <div className="list-row" key={user.id}>
                <div>
                  <strong>{user.displayName}</strong>
                  <span>{user.username} · {roleLabels[user.role]} · {user.active ? 'ativo' : 'inativo'}{user.mustChangePassword ? ' · troca de senha pendente' : ''}</span>
                </div>
                {user.active && (
                  <div className="row-actions">
                    <button className="icon-link" title="Resetar senha" onClick={() => { setResetTarget(user); setTemporaryPassword(''); }}>
                      <KeyRound size={16} />
                    </button>
                    <button className="icon-link" title="Inativar usuário" onClick={() => deactivateUser(user.id)}>
                      <Trash2 size={16} />
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
          {resetTarget && (
            <form className="form-grid reset-password-box" onSubmit={resetPassword}>
              <label className="full">Senha temporária para {resetTarget.displayName}
                <input autoFocus required minLength={10} type="password" value={temporaryPassword} onChange={(event) => setTemporaryPassword(event.target.value)} />
                <span className="field-hint">O usuário será obrigado a trocar essa senha no próximo login.</span>
              </label>
              <div className="form-actions">
                <button type="button" className="secondary" onClick={() => setResetTarget(null)}>Cancelar</button>
                <button type="submit"><KeyRound size={16} /> Resetar senha</button>
              </div>
            </form>
          )}
        </section>

        <section className="panel">
          <div className="section-title"><h2>Novo usuário</h2></div>
          <form className="form-grid" onSubmit={createUser}>
            <label>Usuário
              <input required value={userForm.username} onChange={(event) => setUserForm({ ...userForm, username: event.target.value })} />
            </label>
            <label>Nome
              <input required value={userForm.displayName} onChange={(event) => setUserForm({ ...userForm, displayName: event.target.value })} />
            </label>
            <label>Senha
              <input required minLength={10} type="password" value={userForm.password} onChange={(event) => setUserForm({ ...userForm, password: event.target.value })} />
              <span className="field-hint">Mínimo de 10 caracteres, com maiúscula, minúscula, número e símbolo.</span>
            </label>
            <label>Perfil
              <select value={userForm.role} onChange={(event) => setUserForm({ ...userForm, role: event.target.value as UserRole })}>
                {roles.map((role) => <option key={role} value={role}>{roleLabels[role]}</option>)}
              </select>
            </label>
            <div className="form-actions">
              <button type="submit"><Plus size={16} /> Criar usuário</button>
            </div>
          </form>
        </section>
      </div>

      <section className="panel">
        <div className="section-title"><h2>Auditoria recente</h2></div>
        <form className="toolbar" onSubmit={(event) => { event.preventDefault(); void loadSecurity(); }}>
          <label>Usuário
            <input value={auditFilters.actor} onChange={(event) => setAuditFilters({ ...auditFilters, actor: event.target.value })} />
          </label>
          <label>Ação
            <input value={auditFilters.action} onChange={(event) => setAuditFilters({ ...auditFilters, action: event.target.value })} />
          </label>
          <label>Recurso
            <input value={auditFilters.resourceType} onChange={(event) => setAuditFilters({ ...auditFilters, resourceType: event.target.value })} />
          </label>
          <button type="submit"><Search size={16} /> Filtrar</button>
        </form>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Data</th>
                <th>Usuário</th>
                <th>Ação</th>
                <th>Recurso</th>
                <th>IP</th>
                <th>Detalhes</th>
              </tr>
            </thead>
            <tbody>
              {audit.map((event) => (
                <tr key={event.id}>
                  <td>{date(event.createdAt)}</td>
                  <td>{event.actor}</td>
                  <td>{event.action}</td>
                  <td>{event.resourceType}</td>
                  <td>{event.ipAddress ?? '-'}</td>
                  <td>{event.details ?? '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}
