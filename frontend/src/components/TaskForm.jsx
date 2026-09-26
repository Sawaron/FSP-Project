import { useState } from 'react';
import { contestApi } from '../api';
import { Alert } from './Ui';

export default function TaskForm({ competitionId, task, onSaved }) {
  const [form, setForm] = useState({ title: task?.title || '', statement: task?.statement || '', maxPoints: task?.maxPoints || 100, sortOrder: task?.sortOrder ?? 0 }); const [error, setError] = useState(''); const [busy, setBusy] = useState(false);
  const field = (name) => (event) => setForm({ ...form, [name]: event.target.value });
  async function submit(event) { event.preventDefault(); setBusy(true); setError(''); try { const body = { title: form.title, statement: form.statement, maxPoints: Number(form.maxPoints), sortOrder: Number(form.sortOrder) }; onSaved(task ? await contestApi.updateTask(competitionId, task.id, task.version, body) : await contestApi.createTask(competitionId, body)); } catch (e) { setError(e.message); } finally { setBusy(false); } }
  return <form className="form-grid" onSubmit={submit}><Alert>{error}</Alert><label className="span-2">Название<input required maxLength="200" value={form.title} onChange={field('title')} /></label><label>Максимальный балл<input type="number" min="1" max="1000000" required value={form.maxPoints} onChange={field('maxPoints')} /></label><label>Порядок<input type="number" min="0" required value={form.sortOrder} onChange={field('sortOrder')} /></label><label className="span-2">Условие<textarea required maxLength="20000" rows="10" value={form.statement} onChange={field('statement')} /></label><div className="form-actions span-2"><button className="btn-primary" disabled={busy}>{busy ? 'Сохраняем…' : 'Сохранить задание'}</button></div></form>;
}
