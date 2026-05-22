import { PeoplePage } from './PeoplePage';
import type { CurrentUser } from '../types/domain';

export function OwnersPage({ currentUser }: { currentUser: CurrentUser }) {
  return <PeoplePage kind="owners" title="Locadores" singular="Locador" currentUser={currentUser} />;
}
