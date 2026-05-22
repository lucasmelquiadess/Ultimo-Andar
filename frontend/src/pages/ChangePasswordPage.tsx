import { KeyRound, Save } from 'lucide-react';
import { FormEvent, useState } from 'react';
import { Notice } from '../components/Notice';
import { api } from '../services/api';
import type { CurrentUser } from '../types/domain';

type Props = {
  user: CurrentUser;
  onChanged: (user: CurrentUser) => void;
  onLogout: () => void;
};

export function ChangePasswordPage({ user, onChanged, onLogout }: Props) {
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setError('');
    if (newPassword !== confirmPassword) {
      setError('A confirmação precisa ser igual à nova senha.');
      return;
    }
    setLoading(true);
    try {
      const updatedUser = await api.changePassword(currentPassword, newPassword);
      onChanged(updatedUser);
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
          <KeyRound size={28} />
          <div>
            <strong>Troca de senha</strong>
            <span>{user.displayName}</span>
          </div>
        </div>
        <Notice message="Defina uma nova senha para continuar." type="info" />
        <Notice message={error} type="error" />
        <form className="form-grid login-form" onSubmit={submit}>
          <label className="full">Senha atual
            <input required type="password" value={currentPassword} onChange={(event) => setCurrentPassword(event.target.value)} />
          </label>
          <label className="full">Nova senha
            <input required minLength={10} type="password" value={newPassword} onChange={(event) => setNewPassword(event.target.value)} />
          </label>
          <label className="full">Confirmar nova senha
            <input required minLength={10} type="password" value={confirmPassword} onChange={(event) => setConfirmPassword(event.target.value)} />
          </label>
          <p className="field-hint full">Use pelo menos 10 caracteres com maiúscula, minúscula, número e símbolo.</p>
          <div className="form-actions">
            <button type="button" className="secondary" onClick={onLogout}>Sair</button>
            <button type="submit" disabled={loading}>
              <Save size={17} /> Salvar senha
            </button>
          </div>
        </form>
      </section>
    </main>
  );
}
