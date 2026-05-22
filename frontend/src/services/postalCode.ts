import type { Address } from '../types/domain';

type ViaCepResponse = {
  cep?: string;
  logradouro?: string;
  complemento?: string;
  bairro?: string;
  localidade?: string;
  uf?: string;
  erro?: boolean;
};

export function postalCodeDigits(value = '') {
  return value.replace(/\D/g, '').slice(0, 8);
}

export function formatPostalCode(value = '') {
  const digits = postalCodeDigits(value);
  if (digits.length <= 5) {
    return digits;
  }
  return `${digits.slice(0, 5)}-${digits.slice(5)}`;
}

export async function fetchAddressByPostalCode(value: string): Promise<Address> {
  const postalCode = postalCodeDigits(value);
  if (postalCode.length !== 8) {
    throw new Error('Informe um CEP com 8 dígitos.');
  }

  const response = await fetch(`https://viacep.com.br/ws/${postalCode}/json/`);
  if (!response.ok) {
    throw new Error('Não foi possível consultar o CEP.');
  }

  const data = await response.json() as ViaCepResponse;
  if (data.erro) {
    throw new Error('CEP não encontrado.');
  }

  return {
    postalCode: formatPostalCode(data.cep ?? postalCode),
    street: data.logradouro ?? '',
    complement: data.complemento ?? '',
    neighborhood: data.bairro ?? '',
    city: data.localidade ?? '',
    state: data.uf ?? '',
    country: 'Brasil'
  };
}

export function mergePostalAddress(current: Address | undefined, found: Address): Address {
  return {
    ...(current ?? {}),
    postalCode: found.postalCode,
    street: found.street || current?.street,
    complement: current?.complement || found.complement,
    neighborhood: found.neighborhood || current?.neighborhood,
    city: found.city || current?.city,
    state: found.state || current?.state,
    country: current?.country || 'Brasil'
  };
}
