import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { athleteApi, ratingApi } from '../api';
import { Alert, Empty, Loader, PageTitle, Pagination } from '../components/Ui';

export default function RatingPage() {
  const [page, setPage] = useState(0); const [data, setData] = useState(null); const [profiles, setProfiles] = useState({}); const [error, setError] = useState('');
  useEffect(() => { setData(null); ratingApi.leaderboard(page, 20).then(async (result) => { setData(result); const loaded = await Promise.all(result.content.map((row) => athleteApi.get(row.athleteId).catch(() => null))); setProfiles(Object.fromEntries(loaded.filter(Boolean).map((p) => [p.id, p]))); }).catch((e) => setError(e.message)); }, [page]);
  return <main className="page"><PageTitle eyebrow="Лидерборд" title="Рейтинг спортсменов" subtitle="Рейтинг рассчитывается по официальным результатам и квалификации." /><Alert>{error}</Alert>{!data && !error && <Loader />}{data?.content.length === 0 && <Empty title="Рейтинг пока не сформирован" text="Он появится после публикации первых результатов." />}{data?.content.length > 0 && <div className="table-wrap"><table><thead><tr><th>Место</th><th>Спортсмен</th><th>Результаты</th><th>Квалификация</th><th>Итого</th></tr></thead><tbody>{data.content.map((row, index) => <tr key={row.snapshotId}><td><span className={`rank rank-${page * 20 + index + 1}`}>{page * 20 + index + 1}</span></td><td><Link to={`/athletes/${row.athleteId}`}>{profiles[row.athleteId]?.fullName || `Спортсмен #${row.athleteId}`}</Link><small>{profiles[row.athleteId]?.city}</small></td><td>{row.resultPoints}</td><td>+{row.qualificationPoints}</td><td><strong className="gold-text">{row.totalPoints}</strong></td></tr>)}</tbody></table></div>}<Pagination page={page} totalPages={data?.totalPages} onChange={setPage} /></main>;
}
