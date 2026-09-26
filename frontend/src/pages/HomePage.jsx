import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { athleteApi, competitionApi } from '../api';
import CompetitionCard from '../components/CompetitionCard';

export default function HomePage() {
  const [stats, setStats] = useState({ athletes: '—', competitions: '—' });
  const [competitions, setCompetitions] = useState([]);
  useEffect(() => {
    Promise.allSettled([athleteApi.list(0, 1), competitionApi.list({ page: 0, size: 3 })]).then(([athletes, contests]) => {
      setStats({ athletes: athletes.value?.totalElements ?? '—', competitions: contests.value?.totalElements ?? '—' });
      setCompetitions(contests.value?.content || []);
    });
  }, []);
  return <>
    <section className="hero-wrap"><div className="hero page"><div className="hero-copy"><span className="eyebrow">Республика Дагестан</span><h1>Платформа для тех, кто превращает <span>код в спорт</span></h1><p>Соревнуйтесь, развивайтесь и представляйте регион на крупнейших турнирах по спортивному программированию.</p><div className="hero-actions"><Link to="/register" className="btn-primary">Стать участником</Link><Link to="/competitions" className="btn-ghost">Смотреть соревнования</Link></div></div><div className="code-orbit"><div className="orbit-main">{'{ }'}</div><span className="orbit-one">01</span><span className="orbit-two">++</span><span className="orbit-three">λ</span></div></div></section>
    <main className="page home-content"><div className="stats-grid"><div className="stat-card"><strong>{stats.athletes}</strong><span>спортсменов</span></div><div className="stat-card"><strong>{stats.competitions}</strong><span>соревнований</span></div><div className="stat-card"><strong>24/7</strong><span>доступ к платформе</span></div></div>
      <section className="section"><div className="section-heading"><div><span className="eyebrow">Ближайшие события</span><h2>Соревнования</h2></div><Link to="/competitions">Смотреть все →</Link></div><div className="card-grid">{competitions.map((item) => <CompetitionCard key={item.id} competition={item} />)}</div>{!competitions.length && <div className="soft-panel">Новые соревнования скоро появятся.</div>}</section>
      <section className="feature-grid"><article><span>01</span><h3>Участвуйте</h3><p>Регистрируйтесь на соревнования и решайте реальные задачи.</p></article><article><span>02</span><h3>Растите</h3><p>Получайте результаты, квалификацию и историю достижений.</p></article><article><span>03</span><h3>Соревнуйтесь</h3><p>Следите за турнирными таблицами и рейтингом Федерации.</p></article></section>
    </main>
  </>;
}
