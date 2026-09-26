import { useEffect, useState } from 'react';

function CompetitionsPage() {
    const [competitions, setCompetitions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetch('http://localhost:8082/api/competitions')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Не удалось загрузить соревнования');
                }

                return response.json();
            })
            .then(data => {
                setCompetitions(data.content);
            })
            .catch(error => {
                setError(error.message);
            })
            .finally(() => {
                setLoading(false);
            });
    }, []);

    if (loading) {
        return (
            <div className="page">
                <div className="page-header">
                    <h1>Соревнования</h1>
                    <p>Загрузка соревнований...</p>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="page">
                <div className="page-header">
                    <h1>Соревнования</h1>
                    <p>{error}</p>
                </div>
            </div>
        );
    }

    return (
        <div className="page">
            <div className="page-header">
                <h1>Соревнования</h1>
                <p>Актуальные и предстоящие соревнования Федерации</p>
            </div>

            <div className="card-grid">
                {competitions.map(competition => (
                    <div className="card" key={competition.id}>
                        <div className="card-title">
                            {competition.title}
                        </div>

                        <div className="card-meta">
                            <span className="badge">
                                {formatDate(competition.startsAt)}
                            </span>

                            <span className="badge green">
                                {competition.registrationOpen
                                    ? 'Регистрация открыта'
                                    : formatStatus(competition.status)}
                            </span>

                            <span className="badge gray">
                                {formatLevel(competition.level)}
                            </span>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

function formatDate(date) {
    if (!date) {
        return 'Дата не указана';
    }

    return new Date(date).toLocaleDateString('ru-RU', {
        day: 'numeric',
        month: 'long',
        year: 'numeric'
    });
}

function formatStatus(status) {
    switch (status) {
        case 'PUBLISHED':
            return 'Опубликовано';

        case 'REGISTRATION_OPEN':
            return 'Регистрация открыта';

        case 'REGISTRATION_CLOSED':
            return 'Регистрация закрыта';

        case 'FINISHED':
            return 'Завершено';

        case 'DRAFT':
            return 'Черновик';

        default:
            return status || 'Статус неизвестен';
    }
}

function formatLevel(level) {
    switch (level) {
        case 'REGIONAL':
            return 'Региональный';

        case 'FEDERAL':
            return 'Федеральный';

        case 'INTERNATIONAL':
            return 'Международный';

        default:
            return level || 'Уровень не указан';
    }
}

export default CompetitionsPage;