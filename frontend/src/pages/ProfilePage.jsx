import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  getMyProfile,
  createMyProfile,
  getOrganizations,
  getQualifications
} from '../api';

export default function ProfilePage() {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [needsProfile, setNeedsProfile] = useState(false);
  const navigate = useNavigate();

  // Списки для выпадающих меню
  const [organizations, setOrganizations] = useState([]);
  const [qualifications, setQualifications] = useState([]);

  // Состояние для формы
  const [formData, setFormData] = useState({
    fullName: '',
    city: '',
    organizationId: '',
    qualificationId: '',
  });

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
      return;
    }

    // Загружаем списки организаций и квалификаций параллельно
    Promise.all([getOrganizations(), getQualifications()])
      .then(([orgs, quals]) => {
        setOrganizations(orgs);
        setQualifications(quals);
      })
      .catch((err) => {
        console.error('Ошибка загрузки списков:', err);
      });

    // Пробуем загрузить профиль
    getMyProfile()
      .then((data) => {
        setProfile(data);
        setLoading(false);
      })
      .catch((err) => {
        if (err.message === 'PROFILE_NOT_FOUND') {
          setNeedsProfile(true);
          setError('');
        } else {
          setError('Не удалось загрузить профиль: ' + err.message);
        }
        setLoading(false);
      });
  }, [navigate]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleCreateProfile = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const newProfile = await createMyProfile({
        fullName: formData.fullName,
        city: formData.city,
        organizationId: Number(formData.organizationId),
        qualificationId: Number(formData.qualificationId),
      });

      setProfile(newProfile);
      setNeedsProfile(false);
    } catch (err) {
      setError(err.message || 'Ошибка при создании профиля');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="page">
        <p style={{ textAlign: 'center', marginTop: '40px' }}>Загрузка...</p>
      </div>
    );
  }

  // 1. Форма создания профиля
  if (needsProfile) {
    return (
      <div className="page auth-page">
        <div className="auth-card" style={{ maxWidth: '600px', margin: '0 auto' }}>
          <h1>Создание профиля</h1>
          <p className="auth-subtitle">
            Заполните данные, чтобы участвовать в соревнованиях Федерации
          </p>

          <form onSubmit={handleCreateProfile} className="auth-form">
            <label>
              ФИО
              <input
                type="text"
                name="fullName"
                value={formData.fullName}
                onChange={handleInputChange}
                placeholder="Иванов Иван Иванович"
                required
              />
            </label>
            <label>
              Город
              <input
                type="text"
                name="city"
                value={formData.city}
                onChange={handleInputChange}
                placeholder="Махачкала"
                required
              />
            </label>
            <label>
              Организация
              <select
                name="organizationId"
                value={formData.organizationId}
                onChange={handleInputChange}
                required
              >
                <option value="">Выберите организацию</option>
                {organizations.map((org) => (
                  <option key={org.id} value={org.id}>
                    {org.name}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Квалификация
              <select
                name="qualificationId"
                value={formData.qualificationId}
                onChange={handleInputChange}
                required
              >
                <option value="">Выберите квалификацию</option>
                {qualifications.map((qual) => (
                  <option key={qual.id} value={qual.id}>
                    {qual.name}
                  </option>
                ))}
              </select>
            </label>

            {error && <div className="auth-error">{error}</div>}

            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? 'Сохранение...' : 'Создать профиль'}
            </button>
          </form>
        </div>
      </div>
    );
  }

  // 2. Ошибка
  if (error) {
    return (
      <div className="page">
        <div className="auth-card" style={{ maxWidth: '600px', margin: '0 auto' }}>
          <div className="auth-error" style={{ textAlign: 'center', fontSize: '16px' }}>
            {error}
          </div>
          <button onClick={() => navigate('/')} className="btn-secondary" style={{ marginTop: '16px', width: '100%' }}>
            На главную
          </button>
        </div>
      </div>
    );
  }

  // 3. Успешно загруженный профиль (Красивая версия)
  return (
    <div className="page">
      <div className="page-header">
        <h1>Личный кабинет</h1>
        <p>Данные профиля спортсмена</p>
      </div>

      <div className="auth-card" style={{ maxWidth: '600px', margin: '0 auto' }}>
        <h2 style={{ marginBottom: '32px', color: 'var(--gold)', borderBottom: '1px solid var(--border)', paddingBottom: '16px' }}>
          {profile.fullName}
        </h2>

        <div className="profile-details">
          <div className="profile-data-row">
            <label>Город</label>
            <p>{profile.city}</p>
          </div>

          <div className="profile-data-row">
            <label>Организация</label>
            <p>{profile.organization?.name || 'Не указана'}</p>
          </div>

          <div className="profile-data-row">
            <label>Квалификация</label>
            <p>{profile.qualification?.name || 'Не указана'}</p>
          </div>
        </div>
      </div>
    </div>
  );
}