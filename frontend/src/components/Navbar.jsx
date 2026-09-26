import { useState, useEffect } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';

function Navbar() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const checkAuth = () => {
      const token = localStorage.getItem('token');
      setIsLoggedIn(!!token);
    };

    checkAuth();
    // Слушаем изменения в localStorage (для работы между вкладками)
    window.addEventListener('storage', checkAuth);
    return () => window.removeEventListener('storage', checkAuth);
  }, []);

  const handleLogout = () => {
    // 1. Очищаем все данные авторизации
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('email');
    localStorage.removeItem('role');

    // 2. Уведомляем Navbar и другие вкладки об изменении
    window.dispatchEvent(new Event('storage'));

    // 3. Перенаправляем пользователя на главную страницу
    navigate('/');
  };

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
          <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
            <NavLink to="/profile" className="btn-primary btn-small">
              👤 Профиль
            </NavLink>
            <button
              type="button"
              onClick={handleLogout}
              className="btn-secondary btn-small"
              style={{ cursor: 'pointer', border: 'none' }}
            >
              Выйти
            </button>
          </div>
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