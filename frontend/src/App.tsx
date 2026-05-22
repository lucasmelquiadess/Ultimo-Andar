import { useEffect, useState } from 'react';
import { Shell, type PageKey } from './layouts/Shell';
import { DashboardPage } from './pages/DashboardPage';
import { PropertiesPage } from './pages/PropertiesPage';
import { OwnersPage } from './pages/OwnersPage';
import { TenantsPage } from './pages/TenantsPage';
import { ContractsPage } from './pages/ContractsPage';
import { GenerateContractPage } from './pages/GenerateContractPage';
import { GenerateAddendumPage } from './pages/GenerateAddendumPage';
import { GenerateTerminationPage } from './pages/GenerateTerminationPage';
import { SettingsPage } from './pages/SettingsPage';
import { LoginPage } from './pages/LoginPage';
import { ChangePasswordPage } from './pages/ChangePasswordPage';
import { api } from './services/api';
import type { CurrentUser } from './types/domain';

const titles: Record<PageKey, string> = {
  dashboard: 'Dashboard',
  properties: 'Imóveis',
  owners: 'Locadores',
  tenants: 'Locatários',
  contracts: 'Contratos e documentos',
  'generate-contract': 'Gerar contrato',
  'generate-addendum': 'Gerar aditivo',
  'generate-termination': 'Gerar distrato',
  settings: 'Configurações'
};

const canUsePage = (target: PageKey, user: CurrentUser) => {
  if (target === 'settings') {
    return user.role === 'ADMIN';
  }
  if (['generate-contract', 'generate-addendum', 'generate-termination'].includes(target)) {
    return user.role === 'ADMIN' || user.role === 'OPERATOR';
  }
  return true;
};

export default function App() {
  const [page, setPage] = useState<PageKey>('dashboard');
  const [currentUser, setCurrentUser] = useState<CurrentUser | null>(null);
  const [checkingSession, setCheckingSession] = useState(api.hasToken());

  useEffect(() => {
    if (!api.hasToken()) {
      return;
    }
    api.me()
      .then(setCurrentUser)
      .catch(() => api.logout())
      .finally(() => setCheckingSession(false));
  }, []);

  const navigate = (target: PageKey) => {
    if (currentUser && canUsePage(target, currentUser)) {
      setPage(target);
    }
  };

  const logout = () => {
    api.logout();
    setCurrentUser(null);
    setPage('dashboard');
  };

  const render = () => {
    if (!currentUser) {
      return null;
    }
    switch (page) {
      case 'dashboard':
        return <DashboardPage onNavigate={navigate} currentUser={currentUser} />;
      case 'properties':
        return <PropertiesPage currentUser={currentUser} />;
      case 'owners':
        return <OwnersPage currentUser={currentUser} />;
      case 'tenants':
        return <TenantsPage currentUser={currentUser} />;
      case 'contracts':
        return <ContractsPage currentUser={currentUser} />;
      case 'generate-contract':
        return <GenerateContractPage />;
      case 'generate-addendum':
        return <GenerateAddendumPage />;
      case 'generate-termination':
        return <GenerateTerminationPage />;
      case 'settings':
        return <SettingsPage />;
      default:
        return <DashboardPage onNavigate={navigate} currentUser={currentUser} />;
    }
  };

  if (checkingSession) {
    return <main className="login-page"><p className="empty">Verificando sessão...</p></main>;
  }

  if (!currentUser) {
    return <LoginPage onAuthenticated={setCurrentUser} />;
  }

  if (currentUser.mustChangePassword) {
    return <ChangePasswordPage user={currentUser} onChanged={setCurrentUser} onLogout={logout} />;
  }

  if (!canUsePage(page, currentUser)) {
    setPage('dashboard');
    return null;
  }

  return (
    <Shell active={page} currentUser={currentUser} onNavigate={navigate} onLogout={logout}>
      <header className="page-header">
        <div>
          <span className="eyebrow">Administradora Último Andar</span>
          <h1>{titles[page]}</h1>
        </div>
      </header>
      {render()}
    </Shell>
  );
}
