import { useEffect, useState } from 'react';
import { getAthleteProfiles } from '../api';

function AthletesPage() {
const [profiles, setProfiles] = useState([]);
const [status, setStatus] = useState('loading');

useEffect(() => {
getAthleteProfiles()
.then(data => {
setProfiles(data);
setStatus('ready');
})
.catch(() => setStatus('error'));
}, []);

return (
<div className="page">
    <div className="page-header">
        <h1>Спортсмены</h1>
        <p>Профили зарегистрированных участников Федерации</p>
    </div>

    {status === 'loading' && <div className="loading">Загрузка…</div>}
    {status === 'error' && <div className="error">Не удалось загрузить данные</div>}
    {status === 'ready' && profiles.length === 0 && (
    <div className="empty">Пока нет зарегистрированных спортсменов</div>
    )}

    {status === 'ready' && profiles.length > 0 && (
    <div className="card-grid">
        {profiles.map((p, i) => (
        <div className="card" key={i}>
            <div className="card-title">{p.fullName}</div>
            <div className="card-meta">
                <span className="badge">{p.city}</span>
                {p.qualificationId && <span className="badge green">Квалификация #{p.qualificationId}</span>}
                {p.organizationId && <span className="badge gray">Организация #{p.organizationId}</span>}
            </div>
        </div>
        ))}
    </div>
    )}
</div>
);
}

export default AthletesPage;