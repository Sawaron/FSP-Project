import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getMyProfile } from '../api';

export default function ProfilePage() {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
      return;
    }

    getMyProfile()
      .then((data) => {
        setProfile(data);
        setLoading(false);
      })
      .catch((err) => {
        if (err.message === 'PROFILE_NOT_FOUND') {
          setError('Профиль еще не создан. Пожалуйста, заполните данные.');
          // Здесь позже можно добавить кнопку "Создать профиль"
        } else {
          setError('Не удалось загрузить профиль. ' + err.message);
        }
        setLoading(false);
      });
  }, [navigate]);

  if (loading) return <div className="page"><p>Загрузка профиля...</p></div>;

  return (
    <div className="page">
      <div className="page-header">
        <h1>Личный кабинет</h1>
        <p>Данные профиля спортсмена</p>
      </div>

      {error ? (
        <div className="auth-card" style={{ maxWidth: '600px', margin: '0 auto' }}>
          <div className="auth-error" style={{ textAlign: 'center', fontSize: '16px' }}>
            {error}
          </div>
        </div>
      ) : (
        <div className="auth-card" style={{ maxWidth: '600px', margin: '0 auto' }}>
          <h2 style={{ marginBottom: '24px', color: 'var(--gold)' }}>
            {profile.fullName}
          </h2>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div>
              <label style={{ color: 'var(--text-muted)', fontSize: '13px' }}>Город</label>
              <p style={{ margin: '4px 0 0', fontSize: '16px' }}>{profile.city}</p>
            </div>

            <div>
              <label style={{ color: 'var(--text-muted)', fontSize: '13px' }}>Организация</label>
              <p style={{ margin: '4px 0 0', fontSize: '16px' }}>
                {profile.organization?.name || 'Не указана'}
              </p>
            </div>


            <div>
              <label style={{ color: 'var(--text-muted)', fontSize: '13px' }}>Квалификация</label>
              <p style={{ margin: '4px 0 0', fontSize: '16px' }}>
                {profile.qualification?.name || 'Не указана'}
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}