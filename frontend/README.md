# FSP frontend

Полный интерфейс платформы Федерации спортивного программирования Республики Дагестан.

## Запуск

Backend должен работать на `http://localhost:8082`. Затем:

```bash
npm install
npm run dev
```

Vite проксирует запросы `/api` на backend, поэтому отдельная CORS-настройка для локальной разработки не требуется.

Для другого адреса API создайте `.env.local`:

```text
VITE_API_URL=https://example.com/api
```

## Проверка

```bash
npm run lint
npm run build
```
