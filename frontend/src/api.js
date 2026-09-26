const BASE_URL = 'http://localhost:8080/api';

export async function getAthleteProfiles() {
  const res = await fetch(`${BASE_URL}/athlete-profiles`);
  if (!res.ok) throw new Error('Ошибка загрузки профилей');
  return res.json();
}

export async function getAthleteProfile(id) {
  const res = await fetch(`${BASE_URL}/athlete-profiles/${id}`);
  if (!res.ok) throw new Error('Профиль не найден');
  return res.json();
}export async function registerUser(email, password) {
   const res = await fetch(`${BASE_URL}/auth/register`, {
     method: 'POST',
     headers: { 'Content-Type': 'application/json' },
     body: JSON.stringify({ email, password }),
   });
   const data = await res.json();
   if (!res.ok) {
     throw new Error(data.message || 'Ошибка регистрации');
   }
   return data;
 }

 export async function loginUser(email, password) {
   const res = await fetch(`${BASE_URL}/auth/login`, {
     method: 'POST',
     headers: { 'Content-Type': 'application/json' },
     body: JSON.stringify({ email, password }),
   });
   const data = await res.json();
   if (!res.ok) {
     throw new Error(data.message || 'Неверный email или пароль');
   }
   return data;
 }
