import type { AddendumType, ContractStatus, DocumentType, GuaranteeType, LeaseTermType, PersonType, PropertyStatus, PropertyType } from '../types/domain';

export const currency = (value?: number) =>
  new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value ?? 0);

export const formatCurrencyInput = (value?: number) => currency(value);

export const parseCurrencyInput = (value: string) => Number(onlyDigits(value)) / 100;

export const date = (value?: string) => {
  if (!value) return 'Não informado';
  return new Intl.DateTimeFormat('pt-BR', { timeZone: 'UTC' }).format(new Date(value));
};

export const onlyDigits = (value: string) => value.replace(/\D/g, '');

export const maskCpfCnpj = (value: string) => {
  const digits = onlyDigits(value).slice(0, 14);
  if (digits.length <= 11) {
    return digits
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
  }
  return digits
    .replace(/^(\d{2})(\d)/, '$1.$2')
    .replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3')
    .replace(/\.(\d{3})(\d)/, '.$1/$2')
    .replace(/(\d{4})(\d)/, '$1-$2');
};

export const maskSensitiveCpfCnpj = (value: string) => {
  const digits = onlyDigits(value);
  if (digits.length === 11) {
    return `***.***.***-${digits.slice(-2)}`;
  }
  if (digits.length === 14) {
    return `**.***.***/****-${digits.slice(-2)}`;
  }
  return 'Documento protegido';
};

export const labels: Record<PersonType | PropertyType | PropertyStatus | ContractStatus | LeaseTermType | GuaranteeType | DocumentType | AddendumType, string> = {
  PHYSICAL: 'Pessoa física',
  LEGAL: 'Pessoa jurídica',
  APARTMENT: 'Apartamento',
  HOUSE: 'Casa',
  COMMERCIAL_ROOM: 'Sala comercial',
  STORE: 'Loja',
  LAND: 'Terreno',
  OTHER: 'Outro',
  AVAILABLE: 'Disponível',
  RENTED: 'Alugado',
  INACTIVE: 'Inativo',
  MAINTENANCE: 'Em manutenção',
  ACTIVE: 'Ativo',
  CLOSED: 'Encerrado',
  CANCELED: 'Cancelado',
  EXPIRED: 'Vencido',
  MONTHS_12: '12 meses',
  MONTHS_24: '24 meses',
  MONTHS_36: '36 meses',
  INDETERMINATE: 'Prazo indeterminado',
  CAUTION: 'Caução',
  GUARANTOR: 'Fiador',
  INSURANCE_BOND: 'Seguro fiança',
  NONE: 'Sem garantia',
  LEASE_CONTRACT: 'Contrato',
  ADDENDUM: 'Aditivo',
  TERMINATION: 'Distrato',
  VALUE_CHANGE: 'Alteração de valor',
  TERM_EXTENSION: 'Prorrogação de prazo',
  PARTIES_CHANGE: 'Alteração de partes',
  CLAUSES_CHANGE: 'Alteração de cláusulas'
};
