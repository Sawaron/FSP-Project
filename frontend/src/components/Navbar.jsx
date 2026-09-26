import { useState, useEffect } from 'react';
import { NavLink } from 'react-router-dom';

function Navbar() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const checkAuth = () => {
      const token = localStorage.getItem('token');
      setIsLoggedIn(!!token);
    };

    checkAuth();
    window.addEventListener('storage', checkAuth);
    return () => window.removeEventListener('storage', checkAuth);
  }, []);

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
        {isLoggedIn ? (
          <NavLink to="/profile" className="btn-primary btn-small">
            👤 Профиль
          </NavLink>
        ) : (
          <>
            <NavLink to="/login" className="btn-secondary btn-small">
              Вход
            </NavLink>
            <NavLink to="/register" className="btn-primary btn-small">
              Регистрация
            </NavLink>
          </>
        )}
      </div>
    </nav>
  );
}

export default Navbar;