import { Link } from 'react-router-dom';
import { formatDate, label } from '../utils';
import { StatusBadge } from './Ui';

export default function CompetitionCard({ competition, management = false }) {
  return <Link className="competition-card" to={management ? `/organizer/competitions/${competition.id}` : `/competitions/${competition.id}`}>
    <div className="card-top"><StatusBadge value={competition.status} /><span className="muted">{label(competition.format)}</span></div>
    <h3>{competition.title}</h3><p className="card-description">{competition.description || 'Описание соревнования скоро появится'}</p>
    <div className="card-facts"><span>◆ {label(competition.level)}</span><span>◷ {formatDate(competition.startsAt, true)}</span>{competition.venue && <span>⌖ {competition.venue}</span>}</div>
    <div className="card-footer"><span className={competition.registrationOpen ? 'success-text' : 'muted'}>{competition.registrationOpen ? 'Регистрация открыта' : 'Регистрация закрыта'}</span><strong>Подробнее →</strong></div>
  </Link>;
}
