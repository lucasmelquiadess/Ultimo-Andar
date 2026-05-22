import { ArrowRight, ChevronDown, FileCheck2, FilePenLine, FilePlus2, FolderPlus } from 'lucide-react';
import { useEffect, useState } from 'react';
import { Notice } from '../components/Notice';
import { api } from '../services/api';
import type { CurrentUser, Dashboard } from '../types/domain';
import { currency, date } from '../utils/formatters';
import type { PageKey } from '../layouts/Shell';

type Props = {
  onNavigate: (page: PageKey) => void;
  currentUser: CurrentUser;
};

export function DashboardPage({ onNavigate, currentUser }: Props) {
  const [data, setData] = useState<Dashboard>();
  const [error, setError] = useState('');
  const [showGenerateMenu, setShowGenerateMenu] = useState(false);
  const canWrite = currentUser.role === 'ADMIN' || currentUser.role === 'OPERATOR';

  useEffect(() => {
    api.dashboard().then(setData).catch((err) => setError(err.message));
  }, []);

  return (
    <div className="stack">
      <Notice message={error} type="error" />
      <section className="metrics-grid">
        <Metric label="Imóveis cadastrados" value={data?.totalProperties ?? 0} />
        <Metric label="Locadores" value={data?.totalOwners ?? 0} />
        <Metric label="Locatários" value={data?.totalTenants ?? 0} />
        <Metric label="Contratos ativos" value={data?.activeContracts ?? 0} />
        <Metric label="Carteira mensal" value={currency(data?.monthlyRentPortfolio)} />
      </section>

      {canWrite && (
        <section className="quick-actions">
          <div className="menu-action">
            <button onClick={() => setShowGenerateMenu((visible) => !visible)}>
              <FolderPlus size={18} /> Gerar documentos <ChevronDown size={16} />
            </button>
            {showGenerateMenu && (
              <div className="action-menu">
                <button onClick={() => onNavigate('generate-contract')}>
                  <FilePlus2 size={17} /> Contrato
                </button>
                <button onClick={() => onNavigate('generate-addendum')}>
                  <FilePenLine size={17} /> Aditivo
                </button>
                <button onClick={() => onNavigate('generate-termination')}>
                  <FileCheck2 size={17} /> Distrato
                </button>
              </div>
            )}
          </div>
        </section>
      )}

      <div className="two-column">
        <section className="panel">
          <div className="section-title">
            <h2>Contratos próximos do vencimento</h2>
          </div>
          <div className="list">
            {(data?.expiringContracts ?? []).map((item) => (
              <div className="list-row" key={item.contractNumber}>
                <div>
                  <strong>{item.contractNumber}</strong>
                  <span>{item.tenantName}</span>
                </div>
                <small>{date(item.endDate)}</small>
              </div>
            ))}
            {data?.expiringContracts?.length === 0 && <p className="empty">Nenhum vencimento nos próximos 90 dias.</p>}
          </div>
        </section>

        <section className="panel">
          <div className="section-title">
            <h2>Últimos documentos</h2>
            <button className="text-button" onClick={() => onNavigate('contracts')}>
              Ver todos <ArrowRight size={15} />
            </button>
          </div>
          <div className="list">
            {(data?.recentDocuments ?? []).map((item) => (
              <button className="list-row document-row" key={item.id} onClick={() => api.openDocument(item.id)}>
                <div>
                  <strong>{item.title}</strong>
                  <span>{item.contractNumber ?? 'Sem contrato vinculado'}</span>
                </div>
                <small>{date(item.generatedAt)}</small>
              </button>
            ))}
            {data?.recentDocuments?.length === 0 && <p className="empty">Nenhum documento gerado ainda.</p>}
          </div>
        </section>
      </div>
    </div>
  );
}

function Metric({ label, value }: { label: string; value: string | number }) {
  return (
    <div className="metric">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}
