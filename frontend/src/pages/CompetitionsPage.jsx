import { useEffect, useState } from 'react';
import { competitionApi, directoryApi } from '../api';
import CompetitionCard from '../components/CompetitionCard';
import { Alert, Empty, Loader, PageTitle, Pagination } from '../components/Ui';

export default function CompetitionsPage() {
  const [data, setData] = useState(null); const [disciplines, setDisciplines] = useState([]);
  const [filters, setFilters] = useState({ status: '', disciplineId: '', page: 0, size: 9 }); const [error, setError] = useState('');
  useEffect(() => { directoryApi.disciplines().then(setDisciplines).catch(() => {}); }, []);
  useEffect(() => { setData(null); setError(''); competitionApi.list(filters).then(setData).catch((e) => setError(e.message)); }, [filters]);
  return <main className="page"><PageTitle eyebrow="Календарь" title="Соревнования" subtitle="Выберите турнир, подайте заявку и покажите свой лучший результат." /><div className="filter-bar"><select value={filters.status} onChange={(e) => setFilters({ ...filters, status: e.target.value, page: 0 })}><option value="">Все статусы</option><option value="UPCOMING">Предстоящие</option><option value="ONGOING">Идут сейчас</option><option value="COMPLETED">Завершённые</option></select><select value={filters.disciplineId} onChange={(e) => setFilters({ ...filters, disciplineId: e.target.value, page: 0 })}><option value="">Все дисциплины</option>{disciplines.map((d) => <option key={d.id} value={d.id}>{d.name}</option>)}</select></div><Alert>{error}</Alert>{!data && !error && <Loader />}{data?.content.length === 0 && <Empty title="Соревнования не найдены" text="Попробуйте изменить фильтры." />}{data && <div className="card-grid">{data.content.map((item) => <CompetitionCard key={item.id} competition={item} />)}</div>}<Pagination page={filters.page} totalPages={data?.totalPages} onChange={(page) => setFilters({ ...filters, page })} /></main>;
}
