import { Link } from 'react-router-dom';
import { joinClass, label } from '../utils';

export function Loader({ text = 'Загружаем данные…' }) { return <div className="state-box"><span className="spinner" />{text}</div>; }
export function Empty({ title = 'Здесь пока пусто', text, action, to }) {
  return <div className="empty-state"><div className="empty-icon">{'</>'}</div><h3>{title}</h3>{text && <p>{text}</p>}{action && <Link className="btn-primary" to={to}>{action}</Link>}</div>;
}
export function Alert({ children, type = 'error' }) { return children ? <div className={joinClass('alert', `alert-${type}`)}>{children}</div> : null; }
export function StatusBadge({ value }) { return <span className={joinClass('badge', `status-${String(value).toLowerCase()}`)}>{label(value)}</span>; }
export function PageTitle({ eyebrow, title, subtitle, actions }) {
  return <div className="page-title"><div>{eyebrow && <span className="eyebrow">{eyebrow}</span>}<h1>{title}</h1>{subtitle && <p>{subtitle}</p>}</div>{actions && <div className="title-actions">{actions}</div>}</div>;
}
export function Modal({ title, children, onClose, wide = false }) {
  return <div className="modal-backdrop" role="presentation" onMouseDown={onClose}><section className={joinClass('modal', wide && 'modal-wide')} role="dialog" aria-modal="true" onMouseDown={(event) => event.stopPropagation()}><div className="modal-head"><h2>{title}</h2><button className="icon-button" onClick={onClose} aria-label="Закрыть">×</button></div>{children}</section></div>;
}
export function Pagination({ page, totalPages, onChange }) {
  if (!totalPages || totalPages <= 1) return null;
  return <div className="pagination"><button disabled={page === 0} onClick={() => onChange(page - 1)}>Назад</button><span>{page + 1} / {totalPages}</span><button disabled={page + 1 >= totalPages} onClick={() => onChange(page + 1)}>Далее</button></div>;
}
