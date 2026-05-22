import { Edit3, Plus, Search, Trash2 } from 'lucide-react';
import { FormEvent, useEffect, useState } from 'react';
import { Notice } from '../components/Notice';
import { StatusBadge } from '../components/StatusBadge';
import { usePostalCodeLookup } from '../hooks/usePostalCodeLookup';
import { api } from '../services/api';
import { formatPostalCode } from '../services/postalCode';
import type { CurrentUser, Owner, PropertyStatus, PropertyType, RentalProperty } from '../types/domain';
import { currency, labels } from '../utils/formatters';

const blank: RentalProperty = {
  code: '',
  type: 'APARTMENT',
  address: { country: 'Brasil' },
  monthlyRent: 0,
  condominiumFee: 0,
  iptuValue: 0,
  status: 'AVAILABLE',
  ownerId: ''
};

const propertyTypes: PropertyType[] = ['APARTMENT', 'HOUSE', 'COMMERCIAL_ROOM', 'STORE', 'LAND', 'OTHER'];
const statuses: PropertyStatus[] = ['AVAILABLE', 'RENTED', 'INACTIVE', 'MAINTENANCE'];

export function PropertiesPage({ currentUser }: { currentUser: CurrentUser }) {
  const [items, setItems] = useState<RentalProperty[]>([]);
  const [owners, setOwners] = useState<Owner[]>([]);
  const [form, setForm] = useState<RentalProperty>(blank);
  const [search, setSearch] = useState('');
  const [notice, setNotice] = useState('');
  const [error, setError] = useState('');
  const {
    postalCodeLoading,
    updateAddress,
    handlePostalCodeChange,
    lookupPostalCode
  } = usePostalCodeLookup<RentalProperty>(setForm, setNotice, setError);
  const canWrite = currentUser.role === 'ADMIN' || currentUser.role === 'OPERATOR';
  const canDeactivate = currentUser.role === 'ADMIN';

  const load = () => {
    Promise.all([api.properties(search), api.owners()]).then(([properties, ownerList]) => {
      setItems(properties);
      setOwners(ownerList);
      if (!form.ownerId && ownerList[0]?.id) setForm((current) => ({ ...current, ownerId: ownerList[0].id! }));
    }).catch((err) => setError(err.message));
  };

  useEffect(load, []);

  const save = async (event: FormEvent) => {
    event.preventDefault();
    setError('');
    try {
      const saved = await api.saveProperty(form);
      setNotice('Imóvel salvo com sucesso.');
      setForm({ ...blank, ownerId: owners[0]?.id ?? '' });
      setItems((current) => [saved, ...current.filter((item) => item.id !== saved.id)]);
    } catch (err) {
      setError((err as Error).message);
    }
  };

  const remove = async (id?: string) => {
    if (!id || !confirm('Deseja inativar este imóvel?')) return;
    try {
      await api.deleteProperty(id);
      setItems((current) => current.map((item) => item.id === id ? { ...item, status: 'INACTIVE' } : item));
    } catch (err) {
      setError((err as Error).message);
    }
  };

  const uploadPhoto = async (file?: File) => {
    if (!file || !form.id) return;
    setError('');
    try {
      const photo = await api.uploadPropertyPhoto(form.id, file);
      setNotice('Foto enviada com sucesso.');
      setForm({ ...form, photos: [...(form.photos ?? []), photo] });
      setItems((current) => current.map((item) => item.id === form.id ? { ...item, photos: [...(item.photos ?? []), photo] } : item));
    } catch (err) {
      setError((err as Error).message);
    }
  };

  return (
    <div className="two-column wide-left">
      <section className="panel">
        <div className="toolbar">
          <div className="search-box">
            <Search size={17} />
            <input value={search} onChange={(event) => setSearch(event.target.value)} onKeyDown={(event) => event.key === 'Enter' && load()} placeholder="Buscar por código ou endereço" />
          </div>
          <button onClick={load}>Filtrar</button>
        </div>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Código</th>
                <th>Endereço</th>
                <th>Locador</th>
                <th>Aluguel</th>
                <th>Status</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {items.map((item) => (
                <tr key={item.id}>
                  <td><strong>{item.code}</strong><span>{labels[item.type]}</span></td>
                  <td>{[item.address?.street, item.address?.number, item.address?.city].filter(Boolean).join(', ')}</td>
                  <td>{item.ownerName}</td>
                  <td>{currency(item.monthlyRent)}</td>
                  <td><StatusBadge value={item.status} /></td>
                  <td className="actions">
                    {canWrite && (
                      <button title="Editar" onClick={() => setForm(item)}>
                        <Edit3 size={16} />
                      </button>
                    )}
                    {canDeactivate && (
                      <button title="Inativar" onClick={() => remove(item.id)}>
                        <Trash2 size={16} />
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      {canWrite && <section className="panel">
        <div className="section-title"><h2>{form.id ? 'Editar imóvel' : 'Cadastrar imóvel'}</h2></div>
        <Notice message={notice} type="success" />
        <Notice message={error} type="error" />
        <form className="form-grid" onSubmit={save}>
          <label>Código interno
            <input required value={form.code} onChange={(event) => setForm({ ...form, code: event.target.value })} />
          </label>
          <label>Tipo
            <select value={form.type} onChange={(event) => setForm({ ...form, type: event.target.value as PropertyType })}>
              {propertyTypes.map((type) => <option key={type} value={type}>{labels[type]}</option>)}
            </select>
          </label>
          <label>Locador
            <select required value={form.ownerId} onChange={(event) => setForm({ ...form, ownerId: event.target.value })}>
              <option value="">Selecione</option>
              {owners.map((owner) => <option key={owner.id} value={owner.id}>{owner.name}</option>)}
            </select>
          </label>
          <label>Status
            <select value={form.status} onChange={(event) => setForm({ ...form, status: event.target.value as PropertyStatus })}>
              {statuses.map((status) => <option key={status} value={status}>{labels[status]}</option>)}
            </select>
          </label>
          <label>CEP
            <input
              inputMode="numeric"
              maxLength={9}
              value={formatPostalCode(form.address?.postalCode ?? '')}
              onBlur={(event) => lookupPostalCode(event.target.value)}
              onChange={(event) => handlePostalCodeChange(event.target.value)}
            />
            {postalCodeLoading && <span className="field-hint">Consultando CEP...</span>}
          </label>
          <label>Rua
            <input value={form.address?.street ?? ''} onChange={(event) => updateAddress({ street: event.target.value })} />
          </label>
          <label>Número
            <input value={form.address?.number ?? ''} onChange={(event) => updateAddress({ number: event.target.value })} />
          </label>
          <label>Complemento
            <input value={form.address?.complement ?? ''} onChange={(event) => updateAddress({ complement: event.target.value })} />
          </label>
          <label>Bairro
            <input value={form.address?.neighborhood ?? ''} onChange={(event) => updateAddress({ neighborhood: event.target.value })} />
          </label>
          <label>Cidade
            <input value={form.address?.city ?? ''} onChange={(event) => updateAddress({ city: event.target.value })} />
          </label>
          <label>Estado
            <input maxLength={2} value={form.address?.state ?? ''} onChange={(event) => updateAddress({ state: event.target.value.toUpperCase() })} />
          </label>
          <label>Aluguel mensal
            <input type="number" min="0" step="0.01" required value={form.monthlyRent} onChange={(event) => setForm({ ...form, monthlyRent: Number(event.target.value) })} />
          </label>
          <label>Condomínio
            <input type="number" min="0" step="0.01" value={form.condominiumFee ?? 0} onChange={(event) => setForm({ ...form, condominiumFee: Number(event.target.value) })} />
          </label>
          <label>IPTU
            <input type="number" min="0" step="0.01" value={form.iptuValue ?? 0} onChange={(event) => setForm({ ...form, iptuValue: Number(event.target.value) })} />
          </label>
          <label>Descrição
            <textarea value={form.description ?? ''} onChange={(event) => setForm({ ...form, description: event.target.value })} />
          </label>
          <label>Observações internas
            <textarea value={form.internalNotes ?? ''} onChange={(event) => setForm({ ...form, internalNotes: event.target.value })} />
          </label>
          {form.id && (
            <label className="full">Fotos do imóvel
              <input type="file" accept="image/*" onChange={(event) => uploadPhoto(event.target.files?.[0])} />
              <span className="field-hint">{form.photos?.length ?? 0} foto(s) vinculada(s)</span>
            </label>
          )}
          <div className="form-actions">
            <button type="button" className="secondary" onClick={() => setForm({ ...blank, ownerId: owners[0]?.id ?? '' })}>Limpar</button>
            <button type="submit"><Plus size={16} /> Salvar</button>
          </div>
        </form>
      </section>}
    </div>
  );
}
