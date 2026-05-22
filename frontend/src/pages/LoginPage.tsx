import { LockKeyhole, LogIn } from 'lucide-react';
import { FormEvent, useState } from 'react';
import { Notice } from '../components/Notice';
import { api } from '../services/api';
import type { CurrentUser } from '../types/domain';

type Props = {
  onAuthenticated: (user: CurrentUser) => void;
};

export function LoginPage({ onAuthenticated }: Props) {
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setError('');
    setLoading(true);
    try {
      const user = await api.login(username, password);
      onAuthenticated(user);
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="login-page">
      <section className="login-panel">
        <div className="brand-mark login-brand">
          <LockKeyhole size={28} />
          <div>
            <strong>Último Andar</strong>
            <span>Acesso seguro</span>
          </div>
        </div>
        <Notice message={error} type="error" />
        <form className="form-grid login-form" onSubmit={submit}>
          <label className="full">Usuário
            <input autoFocus required value={username} onChange={(event) => setUsername(event.target.value)} />
          </label>
          <label className="full">Senha
            <input required type="password" value={password} onChange={(event) => setPassword(event.target.value)} />
          </label>
          <div className="form-actions">
            <button type="submit" disabled={loading}>
              <LogIn size={17} /> Entrar
            </button>
          </div>
        </form>
      </section>
    </main>
  );
}
