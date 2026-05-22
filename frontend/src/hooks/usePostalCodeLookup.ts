import { useRef, useState } from 'react';
import type { Dispatch, SetStateAction } from 'react';
import type { Address } from '../types/domain';
import {
  fetchAddressByPostalCode,
  formatPostalCode,
  mergePostalAddress,
  postalCodeDigits
} from '../services/postalCode';

type WithAddress = {
  address?: Address;
};

export function usePostalCodeLookup<T extends WithAddress>(
  setForm: Dispatch<SetStateAction<T>>,
  setNotice: Dispatch<SetStateAction<string>>,
  setError: Dispatch<SetStateAction<string>>
) {
  const [postalCodeLoading, setPostalCodeLoading] = useState(false);
  const lastLookup = useRef('');

  const updateAddress = (patch: Partial<Address>) => {
    setForm((current) => ({
      ...current,
      address: {
        country: 'Brasil',
        ...(current.address ?? {}),
        ...patch
      }
    }));
  };

  const lookupPostalCode = async (value?: string) => {
    const postalCode = postalCodeDigits(value);
    if (!postalCode) {
      return;
    }
    if (postalCode.length !== 8) {
      setError('Informe um CEP com 8 dígitos.');
      return;
    }
    if (lastLookup.current === postalCode) {
      return;
    }

    lastLookup.current = postalCode;
    setPostalCodeLoading(true);
    setError('');
    try {
      const found = await fetchAddressByPostalCode(postalCode);
      setForm((current) => ({
        ...current,
        address: mergePostalAddress(current.address, found)
      }));
      setNotice('Endereço preenchido pelo CEP.');
    } catch (err) {
      lastLookup.current = '';
      setError((err as Error).message);
    } finally {
      setPostalCodeLoading(false);
    }
  };

  const handlePostalCodeChange = (value: string) => {
    const formatted = formatPostalCode(value);
    updateAddress({ postalCode: formatted });
    const postalCode = postalCodeDigits(formatted);
    if (postalCode.length < 8) {
      lastLookup.current = '';
    }
    if (postalCode.length === 8) {
      void lookupPostalCode(postalCode);
    }
  };

  return {
    postalCodeLoading,
    updateAddress,
    handlePostalCodeChange,
    lookupPostalCode
  };
}
