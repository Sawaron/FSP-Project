import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { athleteApi, ratingApi } from '../api';
import { Alert, Empty, Loader, PageTitle } from '../components/Ui';
import { formatDate } from '../utils';

export default function AthletePage() {
  const { id } = useParams(); const [athlete, setAthlete] = useState(null); const [rating, setRating] = useState(null); const [results, setResults] = useState([]); const [error, setError] = useState('');
  useEffect(() => { Promise.allSettled([athleteApi.get(id), ratingApi.current(id), athleteApi.results(id)]).then(([a, r, x]) => { if (a.status === 'rejected') setError(a.reason.message); else setAthlete(a.value); setRating(r.value || null); setResults(x.value?.content || []); }); }, [id]);
  if (!athlete && !error) return <main className="page"><Loader /></main>;
  return <main className="page"><Alert>{error}</Alert>{athlete && <><PageTitle eyebrow="Профиль спортсмена" title={athlete.fullName} subtitle={`${athlete.city}${athlete.organization ? ` · ${athlete.organization.name}` : ''}`} /><section className="profile-summary"><div className="profile-avatar">{athlete.fullName.slice(0, 1)}</div><div><span>Квалификация</span><strong>{athlete.qualification?.name || 'Не присвоена'}</strong></div><div><span>Рейтинг</span><strong>{rating?.totalPoints ?? 0}</strong></div><div><span>Результатов</span><strong>{results.length}</strong></div></section><section className="panel"><div className="panel-head"><h2>Результаты</h2></div>{results.length ? <div className="compact-list">{results.map((result) => <Link key={result.id} to={`/competitions/${result.competitionId}`}><div><strong>{result.competitionTitle}</strong><span>{formatDate(result.competitionStartsAt)}</span></div><b>#{result.place} · {result.ratingPoints} баллов</b></Link>)}</div> : <Empty title="Нет опубликованных результатов" />}</section></>}</main>;
}
