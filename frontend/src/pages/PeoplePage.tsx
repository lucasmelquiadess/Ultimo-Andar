import { Edit3, Plus, Search, Trash2 } from 'lucide-react';
import { FormEvent, useEffect, useState } from 'react';
import { Notice } from '../components/Notice';
import { usePostalCodeLookup } from '../hooks/usePostalCodeLookup';
import { api } from '../services/api';
import { formatPostalCode } from '../services/postalCode';
import type { CurrentUser, Owner, Tenant } from '../types/domain';
import { maskCpfCnpj, maskSensitiveCpfCnpj } from '../utils/formatters';

type Kind = 'owners' | 'tenants';
type Person = Owner & Partial<Tenant>;

const blank: Person = {
  personType: 'PHYSICAL',
  name: '',
  document: '',
  address: { country: 'Brasil' },
  active: true
};

export function PeoplePage({ kind, title, singular, currentUser }: { kind: Kind; title: string; singular: string; currentUser: CurrentUser }) {
  const [items, setItems] = useState<Person[]>([]);
  const [form, setForm] = useState<Person>(blank);
  const [search, setSearch] = useState('');
  const [notice, setNotice] = useState('');
  const [error, setError] = useState('');
  const {
    postalCodeLoading,
    updateAddress,
    handlePostalCodeChange,
    lookupPostalCode
  } = usePostalCodeLookup<Person>(setForm, setNotice, setError);
  const canWrite = currentUser.role === 'ADMIN' || currentUser.role === 'OPERATOR';
  const canDeactivate = currentUser.role === 'ADMIN';

  const load = () => {
    const call = kind === 'owners' ? api.owners : api.tenants;
    call(search).then((result) => setItems(result as Person[])).catch((err) => setError(err.message));
  };

  useEffect(load, [kind]);

  const save = async (event: FormEvent) => {
    event.preventDefault();
    setError('');
    try {
      const payload = { ...form, document: maskCpfCnpj(form.document) };
      const saved = kind === 'owners' ? await api.saveOwner(payload) : await api.saveTenant(payload as Tenant);
      setNotice(`${singular} salvo com sucesso.`);
      setForm(blank);
      setItems((current) => [saved as Person, ...current.filter((item) => item.id !== saved.id)]);
    } catch (err) {
      setError((err as Error).message);
    }
  };

  const remove = async (id?: string) => {
    if (!id || !confirm('Deseja inativar este registro?')) return;
    try {
      kind === 'owners' ? await api.deleteOwner(id) : await api.deleteTenant(id);
      setItems((current) => current.filter((item) => item.id !== id));
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
            <input value={search} onChange={(event) => setSearch(event.target.value)} onKeyDown={(event) => event.key === 'Enter' && load()} placeholder="Buscar por nome, CPF ou CNPJ" />
          </div>
          <button onClick={load}>Filtrar</button>
        </div>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Nome</th>
                <th>Documento</th>
                <th>Contato</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {items.map((item) => (
                <tr key={item.id}>
                  <td>
                    <strong>{item.name}</strong>
                    <span>{item.personType === 'PHYSICAL' ? 'Pessoa física' : 'Pessoa jurídica'}</span>
                  </td>
                  <td>{maskSensitiveCpfCnpj(item.document)}</td>
                  <td>{item.email || item.phone || 'Não informado'}</td>
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
        <div className="section-title">
          <h2>{form.id ? 'Editar' : 'Cadastrar'} {singular.toLowerCase()}</h2>
        </div>
        <Notice message={notice} type="success" />
        <Notice message={error} type="error" />
        <form className="form-grid" onSubmit={save}>
          <label>Tipo
            <select value={form.personType} onChange={(event) => setForm({ ...form, personType: event.target.value as Person['personType'] })}>
              <option value="PHYSICAL">Pessoa física</option>
              <option value="LEGAL">Pessoa jurídica</option>
            </select>
          </label>
          <label>Nome ou razão social
            <input required value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} />
          </label>
          <label>CPF/CNPJ
            <input required value={maskCpfCnpj(form.document)} onChange={(event) => setForm({ ...form, document: event.target.value })} />
          </label>
          <label>RG/IE
            <input value={form.identityNumber ?? ''} onChange={(event) => setForm({ ...form, identityNumber: event.target.value })} />
          </label>
          <label>E-mail
            <input type="email" value={form.email ?? ''} onChange={(event) => setForm({ ...form, email: event.target.value })} />
          </label>
          <label>Telefone
            <input value={form.phone ?? ''} onChange={(event) => setForm({ ...form, phone: event.target.value })} />
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
          {kind === 'tenants' && (
            <>
              <label>Dados do cônjuge
                <textarea value={form.spouseData ?? ''} onChange={(event) => setForm({ ...form, spouseData: event.target.value })} />
              </label>
              <label>Dados do fiador
                <textarea value={form.guarantorData ?? ''} onChange={(event) => setForm({ ...form, guarantorData: event.target.value })} />
              </label>
            </>
          )}
          {kind === 'owners' && (
            <label>Dados bancários
              <textarea value={form.bankDetails ?? ''} onChange={(event) => setForm({ ...form, bankDetails: event.target.value })} />
            </label>
          )}
          <label>Observações
            <textarea value={form.notes ?? ''} onChange={(event) => setForm({ ...form, notes: event.target.value })} />
          </label>
          <div className="form-actions">
            <button type="button" className="secondary" onClick={() => setForm(blank)}>Limpar</button>
            <button type="submit"><Plus size={16} /> Salvar</button>
          </div>
        </form>
      </section>}
    </div>
  );
}
