import {
  Building2,
  FileCheck2,
  FilePenLine,
  FilePlus2,
  FileText,
  LayoutDashboard,
  LogOut,
  ScrollText,
  Settings,
  UsersRound
} from 'lucide-react';
import type { ReactNode } from 'react';
import type { CurrentUser, UserRole } from '../types/domain';

export type PageKey =
  | 'dashboard'
  | 'properties'
  | 'owners'
  | 'tenants'
  | 'contracts'
  | 'generate-contract'
  | 'generate-addendum'
  | 'generate-termination'
  | 'settings';

const writerRoles: UserRole[] = ['ADMIN', 'OPERATOR'];

const items: Array<{ key: PageKey; label: string; icon: typeof LayoutDashboard; roles?: UserRole[] }> = [
  { key: 'dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { key: 'properties', label: 'Imóveis', icon: Building2 },
  { key: 'owners', label: 'Locadores', icon: UsersRound },
  { key: 'tenants', label: 'Locatários', icon: UsersRound },
  { key: 'contracts', label: 'Contratos', icon: FileText },
  { key: 'generate-contract', label: 'Gerar Contrato', icon: FilePlus2, roles: writerRoles },
  { key: 'generate-addendum', label: 'Gerar Aditivo', icon: FilePenLine, roles: writerRoles },
  { key: 'generate-termination', label: 'Gerar Distrato', icon: FileCheck2, roles: writerRoles },
  { key: 'settings', label: 'Configurações', icon: Settings, roles: ['ADMIN'] }
];

type Props = {
  active: PageKey;
  currentUser: CurrentUser;
  onNavigate: (page: PageKey) => void;
  onLogout: () => void;
  children: ReactNode;
};

export function Shell({ active, currentUser, onNavigate, onLogout, children }: Props) {
  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand-mark">
          <ScrollText size={26} />
          <div>
            <strong>Último Andar</strong>
            <span>Gestão de locações</span>
          </div>
        </div>
        <nav>
          {items.filter((item) => !item.roles || item.roles.includes(currentUser.role)).map((item) => {
            const Icon = item.icon;
            return (
              <button
                key={item.key}
                className={active === item.key ? 'nav-item active' : 'nav-item'}
                onClick={() => onNavigate(item.key)}
                title={item.label}
              >
                <Icon size={18} />
                <span>{item.label}</span>
              </button>
            );
          })}
        </nav>
        <div className="sidebar-user">
          <span>{currentUser.displayName}</span>
          <small>{currentUser.role}</small>
          <button className="nav-item" onClick={onLogout} title="Sair">
            <LogOut size={18} />
            <span>Sair</span>
          </button>
        </div>
      </aside>
      <main className="main-area">{children}</main>
    </div>
  );
}
