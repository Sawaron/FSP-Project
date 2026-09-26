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
}