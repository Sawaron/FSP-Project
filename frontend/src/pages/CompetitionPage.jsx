import { useCallback, useEffect, useMemo, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { athleteApi, competitionApi, contestApi } from '../api';
import { useAuth } from '../auth';
import { Alert, Empty, Loader, Modal, PageTitle, StatusBadge } from '../components/Ui';
import { formatDate, label } from '../utils';

export default function CompetitionPage() {
  const { id } = useParams(); const { user } = useAuth(); const navigate = useNavigate();
  const [competition, setCompetition] = useState(null); const [registration, setRegistration] = useState(null); const [tasks, setTasks] = useState([]); const [submissions, setSubmissions] = useState([]); const [standings, setStandings] = useState(null); const [answerTask, setAnswerTask] = useState(null); const [answer, setAnswer] = useState(''); const [error, setError] = useState(''); const [notice, setNotice] = useState(''); const [busy, setBusy] = useState(false);
  const load = useCallback(async () => {
    setError('');
    try {
      const current = await competitionApi.get(id); setCompetition(current);
      if (user?.role === 'ATHLETE') {
        const regs = await athleteApi.registrations().catch(() => ({ content: [] }));
        setRegistration(regs.content.find((item) => String(item.competitionId) === String(id) && item.status === 'REGISTERED') || null);
        if (['ONGOING', 'COMPLETED'].includes(current.status)) {
          const [taskResult, submissionResult] = await Promise.allSettled([contestApi.tasks(id), contestApi.mySubmissions(id)]);
          setTasks(taskResult.value || []); setSubmissions(submissionResult.value || []);
        }
      }
      if (current.finalizedAt && user) setStandings(await contestApi.standings(id).catch(() => null));
    } catch (e) { setError(e.message); }
  }, [id, user]);
  useEffect(() => { load(); }, [load]);
  const submitted = useMemo(() => new Map(submissions.map((item) => [item.taskId, item])), [submissions]);
  async function register() { if (!user) return navigate('/login', { state: { from: `/competitions/${id}` } }); setBusy(true); setError(''); try { await competitionApi.register(id); setNotice('Заявка успешно подана'); await load(); } catch (e) { setError(e.message); } finally { setBusy(false); } }
  async function cancel() { if (!confirm('Отменить заявку на соревнование?')) return; setBusy(true); try { await competitionApi.cancelRegistration(registration.id); setRegistration(null); setNotice('Заявка отменена'); } catch (e) { setError(e.message); } finally { setBusy(false); } }
  async function submitAnswer(event) { event.preventDefault(); setBusy(true); setError(''); try { await contestApi.submit(id, answerTask.id, answer); setAnswerTask(null); setAnswer(''); setNotice('Решение отправлено и ожидает проверки'); await load(); } catch (e) { setError(e.message); } finally { setBusy(false); } }
  if (!competition && !error) return <main className="page"><Loader /></main>;
  return <main className="page"><Alert>{error}</Alert><Alert type="success">{notice}</Alert>{competition && <>
    <PageTitle eyebrow={label(competition.level)} title={competition.title} subtitle={`${formatDate(competition.startsAt, true)} — ${formatDate(competition.endsAt, true)}`} actions={<StatusBadge value={competition.status} />} />
    <div className="competition-layout"><div className="content-column"><section className="panel prose"><h2>О соревновании</h2><p>{competition.description}</p><h3>Правила</h3><p className="pre-line">{competition.rules}</p></section>
      {user?.role === 'ATHLETE' && ['ONGOING', 'COMPLETED'].includes(competition.status) && <section className="panel"><div className="panel-head"><div><h2>Задания</h2><p>Решение каждого задания можно отправить один раз.</p></div></div>{tasks.length ? <div className="task-list">{tasks.map((task, index) => { const done = submitted.get(task.id); return <article className="task-card" key={task.id}><div className="task-number">{index + 1}</div><div className="task-body"><div className="task-title"><h3>{task.title}</h3><span>{task.maxPoints} баллов</span></div><p className="pre-line">{task.statement}</p>{done ? <div className="submission-result"><StatusBadge value={done.status} /><span>{done.points == null ? 'Баллы ещё не выставлены' : `${done.points} из ${task.maxPoints} баллов`}</span>{done.reviewComment && <small>Комментарий: {done.reviewComment}</small>}</div> : competition.status === 'ONGOING' && <button className="btn-primary" onClick={() => setAnswerTask(task)}>Отправить решение</button>}</div></article>; })}</div> : <Empty title="Задания пока недоступны" />}</section>}
      {standings && <Standings standings={standings} />}
    </div><aside className="sidebar"><section className="panel"><h3>Информация</h3><dl className="facts"><div><dt>Формат</dt><dd>{label(competition.format)}</dd></div><div><dt>Место</dt><dd>{competition.venue || 'Онлайн'}</dd></div><div><dt>Регистрация до</dt><dd>{formatDate(competition.registrationClosesAt, true)}</dd></div></dl>{user?.role === 'ORGANIZER' ? <Link className="btn-primary full" to={`/organizer/competitions/${id}`}>Управлять</Link> : registration ? <><div className="registered-note">✓ Вы участвуете</div>{competition.status === 'UPCOMING' && <button className="btn-danger full" disabled={busy} onClick={cancel}>Отменить заявку</button>}</> : <button className="btn-primary full" disabled={busy || !competition.registrationOpen} onClick={register}>{competition.registrationOpen ? 'Подать заявку' : 'Регистрация закрыта'}</button>}</section></aside></div>
  </>}{answerTask && <Modal title={`Решение: ${answerTask.title}`} onClose={() => setAnswerTask(null)} wide><form className="form-stack" onSubmit={submitAnswer}><p className="muted">Максимум: {answerTask.maxPoints} баллов. После отправки изменить ответ нельзя.</p><label>Ответ<textarea required maxLength="50000" rows="12" value={answer} onChange={(e) => setAnswer(e.target.value)} placeholder="Опишите решение, приложите ссылку на код или вставьте исходный текст…" /></label><div className="form-actions"><button type="button" className="btn-ghost" onClick={() => setAnswerTask(null)}>Отмена</button><button className="btn-primary" disabled={busy}>Отправить</button></div></form></Modal>}</main>;
}

export function Standings({ standings }) {
  return <section className="panel"><div className="panel-head"><div><h2>Итоговая таблица</h2><p>Опубликовано {formatDate(standings.finalizedAt, true)}</p></div></div>{standings.participants.length ? <div className="table-wrap flat"><table><thead><tr><th>Место</th><th>Спортсмен</th><th>Баллы</th></tr></thead><tbody>{standings.participants.map((item) => <tr key={item.registrationId}><td><span className={`rank rank-${item.place}`}>{item.place}</span></td><td><Link to={`/athletes/${item.athleteId}`}>{item.fullName}</Link></td><td><strong className="gold-text">{item.points}</strong></td></tr>)}</tbody></table></div> : <Empty title="Нет участников" />}</section>;
}
