// Для macOS лучше использовать 127.0.0.1 вместо localhost, чтобы избежать проблем с IPv6.
const BASE_URL = 'http://127.0.0.1:8082/api';

// Получить список всех профилей (если нужно для публичной страницы)
export async function getAthleteProfiles() {
  const res = await fetch(`${BASE_URL}/athletes`); // Проверь точный путь на бэке
  if (!res.ok) throw new Error('Ошибка загрузки профилей');
  return res.json();
}

// Получить профиль ТЕКУЩЕГО авторизованного пользователя
export async function getMyProfile() {
  const token = localStorage.getItem('token');
  const res = await fetch(`${BASE_URL}/athletes/me/profile`, {
    headers: {
      'Authorization': `Bearer ${token}`,
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

// Получить список всех организаций
export async function getOrganizations() {
  const token = localStorage.getItem('token');
  const res = await fetch(`${BASE_URL}/organizations`, {
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  });

  if (!res.ok) throw new Error('Ошибка загрузки организаций');
  return res.json();
}

// Получить список всех квалификаций
export async function getQualifications() {
  const token = localStorage.getItem('token');
  const res = await fetch(`${BASE_URL}/qualifications`, {
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  });

  if (!res.ok) throw new Error('Ошибка загрузки квалификаций');
  return res.json();
}