import { useCallback, useEffect, useState } from 'react';
import CompetitionCard from '../components/CompetitionCard';
import CompetitionForm from '../components/CompetitionForm';
import { competitionApi } from '../api';
import { Alert, Empty, Loader, Modal, PageTitle } from '../components/Ui';

export default function OrganizerPage() {
  const [data, setData] = useState(null); const [create, setCreate] = useState(false); const [error, setError] = useState('');
  const load = useCallback(() => { setData(null); competitionApi.mine().then(setData).catch((e) => setError(e.message)); }, []);
  useEffect(() => { load(); }, [load]);
  const counts = data?.content.reduce((acc, item) => ({ ...acc, [item.status]: (acc[item.status] || 0) + 1 }), {}) || {};
  return <main className="page"><PageTitle eyebrow="Кабинет организатора" title="Управление соревнованиями" subtitle="Создавайте задания, принимайте решения и публикуйте результаты." actions={<button className="btn-primary" onClick={() => setCreate(true)}>+ Новое соревнование</button>} /><Alert>{error}</Alert><div className="stats-grid compact"><div className="stat-card"><strong>{data?.totalElements ?? '—'}</strong><span>всего</span></div><div className="stat-card"><strong>{counts.UPCOMING || 0}</strong><span>предстоящих</span></div><div className="stat-card"><strong>{counts.ONGOING || 0}</strong><span>активных</span></div><div className="stat-card"><strong>{counts.COMPLETED || 0}</strong><span>завершённых</span></div></div>{!data && !error && <Loader />}{data?.content.length === 0 && <Empty title="Создайте первое соревнование" text="Нажмите «Новое соревнование», добавьте задание и опубликуйте карточку." />}{data && <div className="card-grid">{data.content.map((item) => <CompetitionCard key={item.id} competition={item} management />)}</div>}{create && <Modal title="Новое соревнование" wide onClose={() => setCreate(false)}><CompetitionForm onSaved={() => { setCreate(false); load(); }} /></Modal>}</main>;
}
