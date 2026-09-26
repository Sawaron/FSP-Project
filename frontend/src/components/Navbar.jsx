import { useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth';

export default function Navbar() {
  const { user, signOut } = useAuth();
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();
  const close = () => setOpen(false);
  const logout = () => { signOut(); close(); navigate('/'); };

  return <header className="navbar">
    <NavLink to="/" className="navbar-brand" onClick={close}><span className="navbar-logo">{'</>'}</span><span>ФСП <em>Дагестан</em></span></NavLink>
    <button className="menu-button" onClick={() => setOpen(!open)} aria-label="Открыть меню">☰</button>
    <div className={`nav-shell ${open ? 'open' : ''}`}>
      <nav className="navbar-links">
        <NavLink to="/" end onClick={close}>Главная</NavLink><NavLink to="/athletes" onClick={close}>Спортсмены</NavLink>
        <NavLink to="/competitions" onClick={close}>Соревнования</NavLink><NavLink to="/rating" onClick={close}>Рейтинг</NavLink>
        {user?.role === 'ORGANIZER' && <NavLink to="/organizer" onClick={close}>Управление</NavLink>}
      </nav>
      <div className="navbar-auth">
        {user ? <><NavLink to={user.role === 'ORGANIZER' ? '/organizer' : '/profile'} className="user-chip" onClick={close}><span>{user.email?.slice(0, 1).toUpperCase()}</span><small>{user.email}</small></NavLink><button className="btn-ghost btn-small" onClick={logout}>Выйти</button></>
          : <><NavLink to="/login" className="btn-ghost btn-small" onClick={close}>Вход</NavLink><NavLink to="/register" className="btn-primary btn-small" onClick={close}>Регистрация</NavLink></>}
      </div>
    </div>
  </header>;
}
