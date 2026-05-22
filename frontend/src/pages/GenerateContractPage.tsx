import { FilePlus2 } from 'lucide-react';
import { FormEvent, useEffect, useMemo, useState } from 'react';
import { CurrencyInput } from '../components/CurrencyInput';
import { Notice } from '../components/Notice';
import { api } from '../services/api';
import type { ContractRequest, GuaranteeType, LeaseTermType, Owner, RentalProperty, Tenant } from '../types/domain';
import { currency, labels } from '../utils/formatters';

const initial: ContractRequest = {
  propertyId: '',
  ownerId: '',
  tenantId: '',
  monthlyRent: 0,
  rentDueDay: 10,
  termType: 'MONTHS_12',
  startDate: new Date().toISOString().slice(0, 10),
  adjustmentIndex: 'IPCA',
  guaranteeType: 'GUARANTOR',
  paymentMethod: 'boleto bancário'
};

export function GenerateContractPage() {
  const [owners, setOwners] = useState<Owner[]>([]);
  const [tenants, setTenants] = useState<Tenant[]>([]);
  const [properties, setProperties] = useState<RentalProperty[]>([]);
  const [form, setForm] = useState<ContractRequest>(initial);
  const [loading, setLoading] = useState(false);
  const [notice, setNotice] = useState('');
  const [error, setError] = useState('');
  const [ownerSearch, setOwnerSearch] = useState('');
  const [tenantSearch, setTenantSearch] = useState('');

  useEffect(() => {
    Promise.all([api.owners(), api.tenants(), api.properties()]).then(([ownerList, tenantList, propertyList]) => {
      setOwners(ownerList);
      setTenants(tenantList);
      setProperties(propertyList);
    }).catch((err) => setError(err.message));
  }, []);

  const selectedProperty = useMemo(() => properties.find((item) => item.id === form.propertyId), [properties, form.propertyId]);
  const filteredOwners = useMemo(() => {
    const search = ownerSearch.trim().toLowerCase();
    return owners.filter((owner) => owner.id === form.ownerId || !search || owner.name.toLowerCase().includes(search));
  }, [owners, ownerSearch, form.ownerId]);
  const filteredTenants = useMemo(() => {
    const search = tenantSearch.trim().toLowerCase();
    return tenants.filter((tenant) => tenant.id === form.tenantId || !search || tenant.name.toLowerCase().includes(search));
  }, [tenants, tenantSearch, form.tenantId]);

  const selectProperty = (id: string) => {
    const property = properties.find((item) => item.id === id);
    const owner = owners.find((item) => item.id === property?.ownerId);
    setForm((current) => ({
      ...current,
      propertyId: id,
      ownerId: property?.ownerId ?? current.ownerId,
      monthlyRent: property?.monthlyRent ?? current.monthlyRent
    }));
    if (owner) {
      setOwnerSearch(owner.name);
    }
  };

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setError('');
    setNotice('');
    if (form.rentDueDay > 10) {
      setError('A data de vencimento do aluguel deve ser até o dia 10 de cada mês.');
      return;
    }
    setLoading(true);
    try {
      const contract = await api.generateContract(form);
      setNotice(`Contrato ${contract.contractNumber} gerado e salvo com sucesso.`);
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="panel compact">
      <Notice message={notice} type="success" />
      <Notice message={error} type="error" />
      <form className="form-grid three" onSubmit={submit}>
        <label>Imóvel
          <select required value={form.propertyId} onChange={(event) => selectProperty(event.target.value)}>
            <option value="">Selecione</option>
            {properties.map((property) => <option key={property.id} value={property.id}>{property.code} - {property.address?.street}</option>)}
          </select>
        </label>
        <label>Locador
          <input value={ownerSearch} onChange={(event) => setOwnerSearch(event.target.value)} placeholder="Pesquisar locador" />
          <select required value={form.ownerId} onChange={(event) => setForm({ ...form, ownerId: event.target.value })}>
            <option value="">Selecione</option>
            {filteredOwners.map((owner) => <option key={owner.id} value={owner.id}>{owner.name}</option>)}
          </select>
        </label>
        <label>Locatário
          <input value={tenantSearch} onChange={(event) => setTenantSearch(event.target.value)} placeholder="Pesquisar locatário" />
          <select required value={form.tenantId} onChange={(event) => setForm({ ...form, tenantId: event.target.value })}>
            <option value="">Selecione</option>
            {filteredTenants.map((tenant) => <option key={tenant.id} value={tenant.id}>{tenant.name}</option>)}
          </select>
        </label>
        <label>Valor do aluguel
          <CurrencyInput required value={form.monthlyRent} onChange={(monthlyRent) => setForm({ ...form, monthlyRent })} />
        </label>
        <label>Dia de vencimento
          <input type="number" min="1" max="10" required value={form.rentDueDay} onChange={(event) => setForm({ ...form, rentDueDay: Number(event.target.value) })} />
        </label>
        <label>Prazo
          <select value={form.termType} onChange={(event) => setForm({ ...form, termType: event.target.value as LeaseTermType })}>
            {(['MONTHS_12', 'MONTHS_24', 'MONTHS_36', 'INDETERMINATE'] as LeaseTermType[]).map((term) => <option key={term} value={term}>{labels[term]}</option>)}
          </select>
        </label>
        <label>Data de início
          <input type="date" required value={form.startDate} onChange={(event) => setForm({ ...form, startDate: event.target.value })} />
        </label>
        <label>Índice de reajuste
          <input value={form.adjustmentIndex ?? ''} onChange={(event) => setForm({ ...form, adjustmentIndex: event.target.value })} />
        </label>
        <label>Garantia
          <select value={form.guaranteeType} onChange={(event) => setForm({ ...form, guaranteeType: event.target.value as GuaranteeType })}>
            {(['CAUTION', 'GUARANTOR', 'INSURANCE_BOND', 'NONE', 'OTHER'] as GuaranteeType[]).map((item) => <option key={item} value={item}>{labels[item]}</option>)}
          </select>
        </label>
        <label>Forma de pagamento
          <input value={form.paymentMethod ?? ''} onChange={(event) => setForm({ ...form, paymentMethod: event.target.value })} />
        </label>
        <label>Observações
          <textarea value={form.notes ?? ''} onChange={(event) => setForm({ ...form, notes: event.target.value })} />
        </label>
        <label>Cláusulas extras
          <textarea value={form.extraClauses ?? ''} onChange={(event) => setForm({ ...form, extraClauses: event.target.value })} />
        </label>
        {selectedProperty && <div className="summary-strip">Imóvel selecionado: {selectedProperty.code} | Aluguel sugerido: {currency(selectedProperty.monthlyRent)}</div>}
        <div className="form-actions full">
          <button type="submit" disabled={loading}><FilePlus2 size={17} /> {loading ? 'Gerando PDF...' : 'Gerar contrato'}</button>
        </div>
      </form>
    </section>
  );
}
