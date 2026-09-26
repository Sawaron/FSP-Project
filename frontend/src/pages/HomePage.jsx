import { useEffect, useState } from 'react';
import { getAthleteProfiles } from '../api';

function HomePage() {
  const [count, setCount] = useState(null);

  useEffect(() => {
    getAthleteProfiles()
      .then(data => setCount(data.length))
      .catch(() => setCount(0));
  }, []);

  return (
    <div className="page">
      <div className="hero">
        <span className="hero-badge">Республика Дагестан</span>
        <h1>
          Спортивное программирование —<br />
          <span>возможность для каждого</span>
        </h1>
        <p>
          Платформа Федерации спортивного программирования Республики Дагестан —
          объединяем талантливых разработчиков, создаём сильное сообщество.
        </p>
        <div className="hero-actions">
          <a href="/athletes" className="btn-primary">Присоединиться</a>
          <a href="/competitions" className="btn-secondary">Узнать больше</a>
        </div>

        <div className="stats-grid">
          <div className="stat-card">
            <div className="value">{count ?? '—'}</div>
            <div className="label">Участников</div>
          </div>
          <div className="stat-card">
            <div className="value">—</div>
            <div className="label">Соревнований в год</div>
          </div>
          <div className="stat-card">
            <div className="value">ТехноСпортФест 2026</div>
            <div className="label">Ближайший фестиваль</div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default HomePage;