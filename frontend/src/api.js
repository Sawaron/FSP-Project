const API_URL = import.meta.env.VITE_API_URL || '/api';

function params(values = {}) {
  const query = new URLSearchParams();
  Object.entries(values).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') query.set(key, value);
  });
  const result = query.toString();
  return result ? `?${result}` : '';
}

export async function api(path, options = {}) {
  const token = localStorage.getItem('token');
  const headers = new Headers(options.headers || {});
  if (options.body && !headers.has('Content-Type')) headers.set('Content-Type', 'application/json');
  if (token) headers.set('Authorization', `Bearer ${token}`);

  const response = await fetch(`${API_URL}${path}`, { ...options, headers });
  if (response.status === 204) return null;
  const contentType = response.headers.get('content-type') || '';
  const payload = contentType.includes('application/json')
    ? await response.json().catch(() => null)
    : await response.text().catch(() => '');

  if (!response.ok) {
    const error = new Error(payload?.message || payload?.detail || payload || `Ошибка ${response.status}`);
    error.status = response.status;
    error.code = payload?.code;
    error.details = payload;
    if (response.status === 401) window.dispatchEvent(new Event('auth-expired'));
    throw error;
  }
  return payload;
}

const json = (method, body) => ({ method, body: JSON.stringify(body) });

export const authApi = {
  register: (email, password) => api('/auth/register', json('POST', { email, password })),
  login: (email, password) => api('/auth/login', json('POST', { email, password })),
};

export const directoryApi = {
  organizations: () => api('/organizations'),
  qualifications: () => api('/qualifications'),
  disciplines: () => api('/disciplines'),
};

export const athleteApi = {
  list: (page = 0, size = 20) => api(`/athletes${params({ page, size })}`),
  get: (id) => api(`/athletes/${id}`),
  mine: () => api('/athletes/me/profile'),
  create: (body) => api('/athletes/me/profile', json('POST', body)),
  update: (body) => api('/athletes/me/profile', json('PUT', body)),
  registrations: (page = 0, size = 50) => api(`/athletes/me/registrations${params({ page, size })}`),
  results: (id, page = 0, size = 50) => api(`/athletes/${id}/results${params({ page, size })}`),
};

export const competitionApi = {
  list: (filters = {}) => api(`/competitions${params(filters)}`),
  get: (id) => api(`/competitions/${id}`),
  mine: (page = 0, size = 50) => api(`/competitions/mine${params({ page, size })}`),
  management: (id) => api(`/competitions/${id}/management`),
  create: (body) => api('/competitions', json('POST', body)),
  update: (id, body) => api(`/competitions/${id}`, json('PUT', body)),
  changeStatus: (id, status, version) => api(`/competitions/${id}/status`, json('PATCH', { status, version })),
  remove: (id, version) => api(`/competitions/${id}${params({ version })}`, { method: 'DELETE' }),
  register: (id) => api(`/competitions/${id}/registrations`, { method: 'POST' }),
  participants: (id, page = 0, size = 100) => api(`/competitions/${id}/registrations${params({ page, size })}`),
  cancelRegistration: (id) => api(`/registrations/${id}/cancel`, { method: 'PUT' }),
};

export const contestApi = {
  tasks: (id) => api(`/contests/${id}/tasks`),
  managementTasks: (id) => api(`/contests/${id}/management/tasks`),
  createTask: (id, body) => api(`/contests/${id}/management/tasks`, json('POST', body)),
  updateTask: (id, taskId, version, body) => api(`/contests/${id}/management/tasks/${taskId}${params({ version })}`, json('PUT', body)),
  removeTask: (id, taskId, version) => api(`/contests/${id}/management/tasks/${taskId}${params({ version })}`, { method: 'DELETE' }),
  submit: (id, taskId, answer) => api(`/contests/${id}/tasks/${taskId}/submissions`, json('POST', { answer })),
  mySubmissions: (id) => api(`/contests/${id}/submissions/mine`),
  submissions: (id, filters = {}) => api(`/contests/${id}/management/submissions${params(filters)}`),
  review: (id, submissionId, body) => api(`/contests/${id}/management/submissions/${submissionId}/review`, json('PUT', body)),
  finalize: (id) => api(`/contests/${id}/management/finalize`, { method: 'POST' }),
  standings: (id) => api(`/contests/${id}/standings`),
};

export const ratingApi = {
  leaderboard: (page = 0, size = 50) => api(`/ratings${params({ page, size })}`),
  current: (athleteId) => api(`/ratings/athletes/${athleteId}`),
  history: (athleteId, page = 0, size = 50) => api(`/ratings/athletes/${athleteId}/history${params({ page, size })}`),
};
