export const labels = {
  DRAFT: 'Черновик', UPCOMING: 'Предстоящее', ONGOING: 'Идёт сейчас', COMPLETED: 'Завершено', CANCELLED: 'Отменено',
  ONLINE: 'Онлайн', OFFLINE: 'Очно', HYBRID: 'Гибрид', RUSSIAN_CHAMPIONSHIP: 'Чемпионат России',
  NATIONAL: 'Всероссийский', INTERREGIONAL: 'Межрегиональный', DAGESTAN_CHAMPIONSHIP: 'Чемпионат Дагестана',
  REGIONAL: 'Региональный', REGISTERED: 'Заявка принята', CANCELLED_REGISTRATION: 'Заявка отменена',
  SUBMITTED: 'Ожидает проверки', REVIEWED: 'Проверено', ATHLETE: 'Спортсмен', ORGANIZER: 'Организатор',
};
export const label = (value) => labels[value] || value || '—';
export const formatDate = (value, time = false) => value
  ? new Intl.DateTimeFormat('ru-RU', { dateStyle: 'medium', ...(time ? { timeStyle: 'short' } : {}) }).format(new Date(value))
  : 'Не указано';
export const toLocalInput = (value) => {
  if (!value) return '';
  const date = new Date(value);
  return new Date(date.getTime() - date.getTimezoneOffset() * 60000).toISOString().slice(0, 16);
};
export const toInstant = (value) => value ? new Date(value).toISOString() : null;
export const joinClass = (...values) => values.filter(Boolean).join(' ');
