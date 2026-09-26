const demoCompetitions = [
{ id: 1, title: 'Чемпионат Республики Дагестан по спортивному программированию', status: 'Регистрация открыта', date: '15 октября 2026', level: 'Региональный' },
{ id: 2, title: 'ТехноСпортФест — 2026', status: 'Предстоит', date: '26 сентября 2026', level: 'Фестиваль' },
];

function CompetitionsPage() {
return (
<div className="page">
  <div className="page-header">
    <h1>Соревнования</h1>
    <p>Актуальные и предстоящие соревнования Федерации</p>
  </div>

  <div className="card-grid">
    {demoCompetitions.map(c => (
    <div className="card" key={c.id}>
      <div className="card-title">{c.title}</div>
      <div className="card-meta">
        <span className="badge">{c.date}</span>
        <span className="badge green">{c.status}</span>
        <span className="badge gray">{c.level}</span>
      </div>
    </div>
    ))}
  </div>
</div>
);
}

export default CompetitionsPage;