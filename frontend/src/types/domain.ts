export type PersonType = 'PHYSICAL' | 'LEGAL';
export type PropertyType = 'APARTMENT' | 'HOUSE' | 'COMMERCIAL_ROOM' | 'STORE' | 'LAND' | 'OTHER';
export type PropertyStatus = 'AVAILABLE' | 'RENTED' | 'INACTIVE' | 'MAINTENANCE';
export type ContractStatus = 'ACTIVE' | 'CLOSED' | 'CANCELED' | 'EXPIRED';
export type LeaseTermType = 'MONTHS_12' | 'MONTHS_24' | 'MONTHS_36' | 'INDETERMINATE';
export type GuaranteeType = 'CAUTION' | 'GUARANTOR' | 'INSURANCE_BOND' | 'NONE' | 'OTHER';
export type DocumentType = 'LEASE_CONTRACT' | 'ADDENDUM' | 'TERMINATION';
export type AddendumType = 'VALUE_CHANGE' | 'TERM_EXTENSION' | 'PARTIES_CHANGE' | 'CLAUSES_CHANGE' | 'OTHER';
export type UserRole = 'ADMIN' | 'OPERATOR' | 'READER';

export interface Address {
  postalCode?: string;
  street?: string;
  number?: string;
  complement?: string;
  neighborhood?: string;
  city?: string;
  state?: string;
  country?: string;
}

export interface Owner {
  id?: string;
  personType: PersonType;
  name: string;
  document: string;
  identityNumber?: string;
  nationality?: string;
  maritalStatus?: string;
  profession?: string;
  address?: Address;
  phone?: string;
  email?: string;
  bankDetails?: string;
  notes?: string;
  active?: boolean;
  createdAt?: string;
}

export interface Tenant extends Owner {
  spouseData?: string;
  guarantorData?: string;
}

export interface PropertyPhoto {
  id: string;
  fileName: string;
  originalFileName: string;
  contentType: string;
  sizeBytes: number;
  createdAt: string;
}

export interface RentalProperty {
  id?: string;
  code: string;
  type: PropertyType;
  address?: Address;
  description?: string;
  monthlyRent: number;
  condominiumFee?: number;
  iptuValue?: number;
  status: PropertyStatus;
  ownerId: string;
  ownerName?: string;
  internalNotes?: string;
  photos?: PropertyPhoto[];
  createdAt?: string;
}

export interface ContractRequest {
  propertyId: string;
  ownerId: string;
  tenantId: string;
  monthlyRent: number;
  rentDueDay: number;
  termType: LeaseTermType;
  startDate: string;
  adjustmentIndex?: string;
  guaranteeType: GuaranteeType;
  paymentMethod?: string;
  notes?: string;
  extraClauses?: string;
}

export interface LeaseContract extends ContractRequest {
  id: string;
  contractNumber: string;
  propertyCode: string;
  propertyAddress: string;
  ownerName: string;
  tenantName: string;
  endDate?: string;
  status: ContractStatus;
  generatedAt?: string;
  createdAt?: string;
}

export interface DocumentRecord {
  id: string;
  documentType: DocumentType;
  contractId?: string;
  contractNumber?: string;
  title: string;
  fileName: string;
  contentType: string;
  sizeBytes: number;
  generatedAt: string;
  archived: boolean;
}

export interface AddendumRequest {
  contractId: string;
  addendumType: AddendumType;
  description: string;
  addendumDate: string;
  newMonthlyRent?: number;
  newTerm?: string;
  newEndDate?: string;
  specificChanges?: string;
  observations?: string;
}

export interface TerminationRequest {
  contractId: string;
  terminationDate: string;
  reason: string;
  hasPendingDebts: boolean;
  penaltyAmount?: number;
  proportionalRentAmount?: number;
  pendingChargesAmount?: number;
  repairsAmount?: number;
  observations?: string;
  additionalStatements?: string;
}

export interface TemplateRecord {
  id?: string;
  documentType: DocumentType;
  name: string;
  content: string;
  active?: boolean;
}

export interface CurrentUser {
  username: string;
  displayName: string;
  role: UserRole;
  mustChangePassword: boolean;
}

export interface UserRecord extends CurrentUser {
  id: string;
  active: boolean;
  createdAt: string;
  updatedAt?: string;
}

export interface UserRequest {
  username: string;
  displayName: string;
  password: string;
  role: UserRole;
  active?: boolean;
}

export interface AuditEvent {
  id: string;
  createdAt: string;
  actor: string;
  action: string;
  resourceType: string;
  resourceId?: string;
  details?: string;
  ipAddress?: string;
}

export interface Dashboard {
  totalProperties: number;
  totalOwners: number;
  totalTenants: number;
  activeContracts: number;
  monthlyRentPortfolio: number;
  expiringContracts: Array<{
    contractNumber: string;
    tenantName: string;
    propertyAddress: string;
    endDate: string;
  }>;
  recentDocuments: DocumentRecord[];
}
