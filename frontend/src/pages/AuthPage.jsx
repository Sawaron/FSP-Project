import { useState } from 'react';
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom';
import { authApi } from '../api';
import { useAuth } from '../auth';
import { Alert } from '../components/Ui';

export default function AuthPage({ register = false }) {
  const { user, signIn } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ email: '', password: '', confirmation: '' });
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  if (user) return <Navigate to={user.role === 'ORGANIZER' ? '/organizer' : '/profile'} replace />;

  async function submit(event) {
    event.preventDefault(); setError('');
    if (register && form.password !== form.confirmation) return setError('Пароли не совпадают');
    if (form.password.length < 8) return setError('Пароль должен содержать не менее 8 символов');
    setBusy(true);
    try {
      const data = register ? await authApi.register(form.email, form.password) : await authApi.login(form.email, form.password);
      signIn(data);
      navigate(location.state?.from || (data.role === 'ORGANIZER' ? '/organizer' : '/profile'), { replace: true });
    } catch (e) { setError(e.message); } finally { setBusy(false); }
  }

  return <main className="page auth-page"><section className="auth-card"><span className="eyebrow">Личный кабинет</span><h1>{register ? 'Начните путь спортсмена' : 'С возвращением'}</h1><p>{register ? 'Создайте аккаунт, заполните профиль и подайте первую заявку.' : 'Войдите, чтобы продолжить работу на платформе.'}</p><Alert>{error}</Alert><form className="form-stack" onSubmit={submit}><label>Email<input type="email" required autoComplete="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} placeholder="name@example.com" /></label><label>Пароль<input type="password" required minLength="8" autoComplete={register ? 'new-password' : 'current-password'} value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} placeholder="Минимум 8 символов" /></label>{register && <label>Повторите пароль<input type="password" required value={form.confirmation} onChange={(e) => setForm({ ...form, confirmation: e.target.value })} /></label>}<button className="btn-primary" disabled={busy}>{busy ? 'Подождите…' : register ? 'Создать аккаунт' : 'Войти'}</button></form><div className="auth-switch">{register ? <>Уже зарегистрированы? <Link to="/login">Войти</Link></> : <>Нет аккаунта? <Link to="/register">Зарегистрироваться</Link></>}</div></section></main>;
}
