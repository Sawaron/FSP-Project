import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { athleteApi } from '../api';
import { Alert, Empty, Loader, PageTitle, Pagination } from '../components/Ui';

export default function AthletesPage() {
  const [page, setPage] = useState(0); const [data, setData] = useState(null); const [error, setError] = useState('');
  useEffect(() => { setData(null); athleteApi.list(page, 12).then(setData).catch((e) => setError(e.message)); }, [page]);
  return <main className="page"><PageTitle eyebrow="Сообщество" title="Спортсмены Федерации" subtitle="Участники, которые развивают спортивное программирование в регионе." /><Alert>{error}</Alert>{!data && !error && <Loader />}{data?.content.length === 0 && <Empty title="Профилей пока нет" />}{data && <div className="athlete-grid">{data.content.map((athlete) => <Link to={`/athletes/${athlete.id}`} className="athlete-card" key={athlete.id}><div className="avatar">{athlete.fullName.slice(0, 1)}</div><div><h3>{athlete.fullName}</h3><p>{athlete.city}</p><span>{athlete.qualification?.name || 'Без квалификации'}</span>{athlete.organization && <small>{athlete.organization.name}</small>}</div></Link>)}</div>}<Pagination page={page} totalPages={data?.totalPages} onChange={setPage} /></main>;
}
