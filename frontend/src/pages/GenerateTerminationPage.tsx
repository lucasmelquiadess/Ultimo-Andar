import { FileCheck2 } from 'lucide-react';
import { FormEvent, useEffect, useMemo, useState } from 'react';
import { CurrencyInput } from '../components/CurrencyInput';
import { Notice } from '../components/Notice';
import { api } from '../services/api';
import type { LeaseContract, TerminationRequest } from '../types/domain';
import { currency } from '../utils/formatters';

const initial: TerminationRequest = {
  contractId: '',
  terminationDate: new Date().toISOString().slice(0, 10),
  reason: '',
  hasPendingDebts: false,
  penaltyAmount: 0,
  proportionalRentAmount: 0,
  pendingChargesAmount: 0,
  repairsAmount: 0
};

export function GenerateTerminationPage() {
  const [contracts, setContracts] = useState<LeaseContract[]>([]);
  const [form, setForm] = useState<TerminationRequest>(initial);
  const [loading, setLoading] = useState(false);
  const [notice, setNotice] = useState('');
  const [error, setError] = useState('');
  const [penaltyPercent, setPenaltyPercent] = useState(0);
  const [proportionalRentCount, setProportionalRentCount] = useState<1 | 2 | 3>(1);

  useEffect(() => {
    api.contracts().then(setContracts).catch((err) => setError(err.message));
  }, []);

  const selectedContract = useMemo(() => contracts.find((contract) => contract.id === form.contractId), [contracts, form.contractId]);
  const monthlyRent = selectedContract?.monthlyRent ?? 0;
  const penaltyAmount = form.hasPendingDebts ? monthlyRent * Math.min(Math.max(penaltyPercent, 0), 20) / 100 : 0;
  const proportionalRentAmount = form.hasPendingDebts ? monthlyRent * proportionalRentCount : 0;
  const repairsAmount = form.hasPendingDebts ? form.repairsAmount ?? 0 : 0;
  const pendingChargesAmount = penaltyAmount + proportionalRentAmount + repairsAmount;

  const togglePendingDebts = (checked: boolean) => {
    setForm({
      ...form,
      hasPendingDebts: checked,
      penaltyAmount: checked ? penaltyAmount : 0,
      proportionalRentAmount: checked ? proportionalRentAmount : 0,
      pendingChargesAmount: checked ? pendingChargesAmount : 0,
      repairsAmount: checked ? form.repairsAmount : 0
    });
    if (!checked) {
      setPenaltyPercent(0);
      setProportionalRentCount(1);
    }
  };

  const updatePenaltyPercent = (value: string) => {
    const parsed = Number(value);
    setPenaltyPercent(Number.isNaN(parsed) ? 0 : Math.min(Math.max(parsed, 0), 20));
  };

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setError('');
    setNotice('');
    setLoading(true);
    try {
      await api.generateTermination({
        ...form,
        penaltyAmount,
        proportionalRentAmount,
        pendingChargesAmount,
        repairsAmount
      });
      setNotice('Distrato gerado, contrato encerrado e histórico atualizado.');
      setForm(initial);
      setPenaltyPercent(0);
      setProportionalRentCount(1);
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
          {selectedContract && <span className="field-hint">Aluguel vigente: {currency(selectedContract.monthlyRent)}</span>}
        </label>
        <label>Data de encerramento
          <input type="date" required value={form.terminationDate} onChange={(event) => setForm({ ...form, terminationDate: event.target.value })} />
        </label>
        <label className="checkbox-label">
          <input type="checkbox" checked={form.hasPendingDebts} onChange={(event) => togglePendingDebts(event.target.checked)} />
          Há débitos pendentes
        </label>
        <label>Multa (%)
          <input disabled={!form.hasPendingDebts} type="number" step="0.01" min="0" max="20" value={penaltyPercent} onChange={(event) => updatePenaltyPercent(event.target.value)} />
          <span className="field-hint">Valor calculado: {currency(penaltyAmount)}</span>
        </label>
        <label>Aluguéis proporcionais
          <select disabled={!form.hasPendingDebts} value={proportionalRentCount} onChange={(event) => setProportionalRentCount(Number(event.target.value) as 1 | 2 | 3)}>
            <option value={1}>1 aluguel</option>
            <option value={2}>2 aluguéis</option>
            <option value={3}>3 aluguéis</option>
          </select>
          <span className="field-hint">Valor calculado: {currency(proportionalRentAmount)}</span>
        </label>
        <label>Encargos pendentes
          <CurrencyInput disabled value={pendingChargesAmount} onChange={() => undefined} />
          <span className="field-hint">Multa + aluguéis proporcionais + reparos</span>
        </label>
        <label>Reparos
          <CurrencyInput disabled={!form.hasPendingDebts} value={form.hasPendingDebts ? form.repairsAmount ?? 0 : 0} onChange={(repairs) => setForm({ ...form, repairsAmount: repairs })} />
        </label>
        <label>Motivo do encerramento
          <textarea required value={form.reason} onChange={(event) => setForm({ ...form, reason: event.target.value })} />
        </label>
        <label>Observações
          <textarea value={form.observations ?? ''} onChange={(event) => setForm({ ...form, observations: event.target.value })} />
        </label>
        <label>Declarações adicionais
          <textarea value={form.additionalStatements ?? ''} onChange={(event) => setForm({ ...form, additionalStatements: event.target.value })} />
        </label>
        <div className="form-actions full">
          <button type="submit" disabled={loading}><FileCheck2 size={17} /> {loading ? 'Gerando PDF...' : 'Gerar distrato'}</button>
        </div>
      </form>
    </section>
  );
}
