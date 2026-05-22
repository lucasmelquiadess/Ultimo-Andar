import { PeoplePage } from './PeoplePage';
import type { CurrentUser } from '../types/domain';

export function TenantsPage({ currentUser }: { currentUser: CurrentUser }) {
  return <PeoplePage kind="tenants" title="Locatários" singular="Locatário" currentUser={currentUser} />;
}
