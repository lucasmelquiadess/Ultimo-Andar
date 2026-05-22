import { FilePenLine } from 'lucide-react';
import { FormEvent, useEffect, useState } from 'react';
import { CurrencyInput } from '../components/CurrencyInput';
import { Notice } from '../components/Notice';
import { api } from '../services/api';
import type { AddendumRequest, AddendumType, LeaseContract } from '../types/domain';
import { labels } from '../utils/formatters';

const initial: AddendumRequest = {
  contractId: '',
  addendumType: 'VALUE_CHANGE',
  description: '',
  addendumDate: new Date().toISOString().slice(0, 10)
};

export function GenerateAddendumPage() {
  const [contracts, setContracts] = useState<LeaseContract[]>([]);
  const [form, setForm] = useState<AddendumRequest>(initial);
  const [loading, setLoading] = useState(false);
  const [notice, setNotice] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    api.contracts().then(setContracts).catch((err) => setError(err.message));
  }, []);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setError('');
    setNotice('');
    setLoading(true);
    try {
      await api.generateAddendum(form);
      setNotice('Aditivo gerado e salvo no histórico do contrato.');
      setForm(initial);
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
        <label>Contrato
          <select required value={form.contractId} onChange={(event) => setForm({ ...form, contractId: event.target.value })}>
            <option value="">Selecione</option>
            {contracts.map((contract) => <option key={contract.id} value={contract.id}>{contract.contractNumber} - {contract.tenantName}</option>)}
          </select>
        </label>
        <label>Tipo de aditivo
          <select value={form.addendumType} onChange={(event) => setForm({ ...form, addendumType: event.target.value as AddendumType })}>
            {(['VALUE_CHANGE', 'TERM_EXTENSION', 'PARTIES_CHANGE', 'CLAUSES_CHANGE', 'OTHER'] as AddendumType[]).map((type) => <option key={type} value={type}>{labels[type]}</option>)}
          </select>
        </label>
        <label>Data do aditivo
          <input type="date" required value={form.addendumDate} onChange={(event) => setForm({ ...form, addendumDate: event.target.value })} />
        </label>
        <label>Novo aluguel
          <CurrencyInput value={form.newMonthlyRent ?? 0} onChange={(newMonthlyRent) => setForm({ ...form, newMonthlyRent })} />
        </label>
        <label>Novo prazo
          <input value={form.newTerm ?? ''} onChange={(event) => setForm({ ...form, newTerm: event.target.value })} />
        </label>
        <label>Nova data de término
          <input type="date" value={form.newEndDate ?? ''} onChange={(event) => setForm({ ...form, newEndDate: event.target.value })} />
        </label>
        <label>Descrição da alteração
          <textarea required value={form.description} onChange={(event) => setForm({ ...form, description: event.target.value })} />
        </label>
        <label>Alterações específicas
          <textarea value={form.specificChanges ?? ''} onChange={(event) => setForm({ ...form, specificChanges: event.target.value })} />
        </label>
        <label>Observações
          <textarea value={form.observations ?? ''} onChange={(event) => setForm({ ...form, observations: event.target.value })} />
        </label>
        <div className="form-actions full">
          <button type="submit" disabled={loading}><FilePenLine size={17} /> {loading ? 'Gerando PDF...' : 'Gerar aditivo'}</button>
        </div>
      </form>
    </section>
  );
}
