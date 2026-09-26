const BASE_URL = 'http://localhost:8082/api';

// Получить список всех профилей (если нужно)
export async function getAthleteProfiles() {
  const res = await fetch(`${BASE_URL}/athletes`); // Проверь путь на бэке, если есть такой
  if (!res.ok) throw new Error('Ошибка загрузки профилей');
  return res.json();
}

// Получить профиль ТЕКУЩЕГО авторизованного пользователя
export async function getMyProfile() {
  const token = localStorage.getItem('token');
  const res = await fetch(`${BASE_URL}/athletes/me/profile`, {
    headers: {
      'Authorization': `Bearer ${token}`, // Обязательно для /me эндпоинтов
    },
  });

  if (!res.ok) {
    if (res.status === 404) throw new Error('PROFILE_NOT_FOUND');
    throw new Error('Ошибка загрузки профиля');
  }

  return res.json();
}

// Создать профиль для ТЕКУЩЕГО авторизованного пользователя
export async function createMyProfile(profileData) {
  const token = localStorage.getItem('token');
  const res = await fetch(`${BASE_URL}/athletes/me/profile`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
    body: JSON.stringify(profileData),
    // profileData должен совпадать с твоим Java DTO AthleteProfileRequest
    // например: { fullName, city, organizationId, qualificationId }
  });

  if (!res.ok) {
    const errorData = await res.json().catch(() => ({}));
    throw new Error(errorData.message || 'Ошибка создания профиля');
  }

  return res.json();
}

export async function registerUser(email, password) {
  const res = await fetch(`${BASE_URL}/auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password }),
  });

  const data = await res.json();
  if (!res.ok) throw new Error(data.message || 'Ошибка регистрации');
  return data;
}

export async function loginUser(email, password) {
  const res = await fetch(`${BASE_URL}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password }),
  });

  const data = await res.json();
  if (!res.ok) throw new Error(data.message || 'Неверный email или пароль');
  return data;
}