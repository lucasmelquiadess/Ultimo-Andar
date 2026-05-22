import { Download, FileText, RefreshCcw, Search } from 'lucide-react';
import { useEffect, useState } from 'react';
import { Notice } from '../components/Notice';
import { StatusBadge } from '../components/StatusBadge';
import { api } from '../services/api';
import type { CurrentUser, DocumentRecord, LeaseContract } from '../types/domain';
import { currency, date, labels } from '../utils/formatters';

export function ContractsPage({ currentUser }: { currentUser: CurrentUser }) {
  const [contracts, setContracts] = useState<LeaseContract[]>([]);
  const [documents, setDocuments] = useState<DocumentRecord[]>([]);
  const [search, setSearch] = useState('');
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const canWrite = currentUser.role === 'ADMIN' || currentUser.role === 'OPERATOR';

  const load = () => {
    Promise.all([api.contracts(search), api.documents(search)])
      .then(([contractList, documentList]) => {
        setContracts(contractList);
        setDocuments(documentList);
      })
      .catch((err) => setError(err.message));
  };

  useEffect(load, []);

  const reissue = async (id: string) => {
    setError('');
    try {
      await api.reissueContract(id);
      setNotice('Contrato reemitido e salvo no histórico.');
      load();
    } catch (err) {
      setError((err as Error).message);
    }
  };

  const openDocument = async (id: string, download = false) => {
    setError('');
    try {
      download ? await api.downloadDocument(id) : await api.openDocument(id);
    } catch (err) {
      setError((err as Error).message);
    }
  };

  return (
    <div className="stack">
      <div className="toolbar">
        <div className="search-box">
          <Search size={17} />
          <input value={search} onChange={(event) => setSearch(event.target.value)} onKeyDown={(event) => event.key === 'Enter' && load()} placeholder="Buscar contrato, pessoa ou documento" />
        </div>
        <button onClick={load}>Filtrar</button>
      </div>
      <Notice message={notice} type="success" />
      <Notice message={error} type="error" />

      <section className="panel">
        <div className="section-title"><h2>Contratos</h2></div>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Número</th>
                <th>Imóvel</th>
                <th>Locatário</th>
                <th>Aluguel</th>
                <th>Vigência</th>
                <th>Status</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {contracts.map((contract) => (
                <tr key={contract.id}>
                  <td><strong>{contract.contractNumber}</strong><span>{labels[contract.termType]}</span></td>
                  <td>{contract.propertyCode}</td>
                  <td>{contract.tenantName}</td>
                  <td>{currency(contract.monthlyRent)}</td>
                  <td>{date(contract.startDate)} a {date(contract.endDate)}</td>
                  <td><StatusBadge value={contract.status} /></td>
                  <td className="actions">
                    {canWrite && <button title="Reemitir PDF" onClick={() => reissue(contract.id)}><RefreshCcw size={16} /></button>}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      <section className="panel">
        <div className="section-title"><h2>Documentos gerados</h2></div>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Documento</th>
                <th>Contrato</th>
                <th>Gerado em</th>
                <th>Arquivo</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {documents.map((doc) => (
                <tr key={doc.id}>
                  <td><strong>{doc.title}</strong><span>{labels[doc.documentType]}</span></td>
                  <td>{doc.contractNumber ?? 'Não vinculado'}</td>
                  <td>{date(doc.generatedAt)}</td>
                  <td>{doc.fileName}</td>
                  <td className="actions">
                    <button title="Abrir PDF" onClick={() => openDocument(doc.id)}><FileText size={16} /></button>
                    <button title="Baixar PDF" onClick={() => openDocument(doc.id, true)}><Download size={16} /></button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}
