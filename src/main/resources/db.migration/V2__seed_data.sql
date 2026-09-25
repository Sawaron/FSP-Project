-- Справочники
INSERT INTO disciplines (name, description) VALUES
                                                ('Спортивное программирование', 'Алгоритмическое программирование (олимпиады)'),
                                                ('Продуктовое программирование', 'Хакатоны и разработка ПО');

INSERT INTO qualifications (name, rating_bonus, sort_order) VALUES
                                                                ('Без разряда', 0, 1),
                                                                ('III разряд', 10, 2),
                                                                ('II разряд', 20, 3),
                                                                ('I разряд', 30, 4),
                                                                ('КМС', 50, 5),
                                                                ('МС', 80, 6);

INSERT INTO organizations (name, city) VALUES
                                           ('ДГТУ', 'Махачкала'),
                                           ('ДГУ', 'Махачкала');

-- Пользователи (Пароль для всех: admin123)
-- Организатор
INSERT INTO users (email, password_hash, role, created_at) VALUES
    ('org@technofest.ru', '$2a$10$X8O.U8V/hX48K/G/uTInC.zBwBfE9hT0.QoU/5.F3eM3/6F6U3i5W', 'ORGANIZER', NOW());

-- Спортсмен 1 (уже с заполненным профилем)
INSERT INTO users (email, password_hash, role, created_at) VALUES
    ('athlete@technofest.ru', '$2a$10$X8O.U8V/hX48K/G/uTInC.zBwBfE9hT0.QoU/5.F3eM3/6F6U3i5W', 'ATHLETE', NOW());

INSERT INTO athlete_profiles (user_id, full_name, organization_id, city, qualification_id, created_at, updated_at)
VALUES (2, 'Магомедов Али', 1, 'Махачкала', 4, NOW(), NOW());

-- Тестовое соревнование для показа заявок
INSERT INTO competitions (title, level, discipline_id, starts_at, ends_at, format, venue, description, status, registration_opens_at, registration_closes_at, created_by_user_id)
VALUES (
           'Кубок Дагестана - 2026', 'REGIONAL', 2,
           NOW() + INTERVAL '10 days', NOW() + INTERVAL '12 days',
           'OFFLINE', 'ДГТУ, Точка Кипения', 'Главный хакатон года',
           'UPCOMING', NOW() - INTERVAL '2 days', NOW() + INTERVAL '5 days', 1
       );