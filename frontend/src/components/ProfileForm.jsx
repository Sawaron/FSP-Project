import { useEffect, useState } from 'react';
import { athleteApi, directoryApi } from '../api';
import { Alert } from './Ui';

export default function ProfileForm({ profile, onSaved }) {
  const [form, setForm] = useState({ fullName: profile?.fullName || '', city: profile?.city || '', organizationId: profile?.organization?.id || '', qualificationId: profile?.qualification?.id || '' });
  const [directories, setDirectories] = useState({ organizations: [], qualifications: [] }); const [error, setError] = useState(''); const [busy, setBusy] = useState(false);
  useEffect(() => { Promise.all([directoryApi.organizations(), directoryApi.qualifications()]).then(([organizations, qualifications]) => setDirectories({ organizations, qualifications })).catch((e) => setError(e.message)); }, []);
  const field = (name) => (event) => setForm({ ...form, [name]: event.target.value });
  async function submit(event) { event.preventDefault(); setBusy(true); setError(''); try { const body = { fullName: form.fullName, city: form.city, organizationId: form.organizationId ? Number(form.organizationId) : null, qualificationId: form.qualificationId ? Number(form.qualificationId) : null }; onSaved(profile ? await athleteApi.update(body) : await athleteApi.create(body)); } catch (e) { setError(e.message); } finally { setBusy(false); } }
  return <form className="form-grid" onSubmit={submit}><Alert>{error}</Alert><label className="span-2">Фамилия, имя и отчество<input required maxLength="255" value={form.fullName} onChange={field('fullName')} /></label><label>Город<input required maxLength="255" value={form.city} onChange={field('city')} /></label><label>Организация<select value={form.organizationId} onChange={field('organizationId')}><option value="">Не выбрана</option>{directories.organizations.map((item) => <option key={item.id} value={item.id}>{item.name} · {item.city}</option>)}</select></label><label>Квалификация<select value={form.qualificationId} onChange={field('qualificationId')}><option value="">Не выбрана</option>{directories.qualifications.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}</select></label><div className="form-actions span-2"><button className="btn-primary" disabled={busy}>{busy ? 'Сохраняем…' : profile ? 'Сохранить изменения' : 'Создать профиль'}</button></div></form>;
}
