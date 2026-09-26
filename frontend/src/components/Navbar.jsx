import { NavLink } from 'react-router-dom';

function Navbar() {
  return (
    <nav className="navbar">
      <NavLink to="/" className="navbar-brand">
        <span className="navbar-logo">{'</>'}</span>
        Федерация спортивного программирования РД
      </NavLink>
      <div className="navbar-links">
        <NavLink to="/" end className={({ isActive }) => (isActive ? 'active' : '')}>
          Главная
        </NavLink>
        <NavLink to="/athletes" className={({ isActive }) => (isActive ? 'active' : '')}>
          Спортсмены
        </NavLink>
        <NavLink to="/competitions" className={({ isActive }) => (isActive ? 'active' : '')}>
          Соревнования
        </NavLink>
        <NavLink to="/rating" className={({ isActive }) => (isActive ? 'active' : '')}>
          Рейтинг
        </NavLink>
      </div>
      <div className="navbar-auth">
        <NavLink to="/login" className="btn-secondary btn-small">
          Вход
        </NavLink>
        <NavLink to="/register" className="btn-primary btn-small">
          Регистрация
        </NavLink>
      </div>
    </nav>
  );
}

export default Navbar;
