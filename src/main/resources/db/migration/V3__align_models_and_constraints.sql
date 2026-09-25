ALTER TABLE users ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT TRUE;
UPDATE users SET enabled = FALSE
WHERE email IN ('org@technofest.ru', 'athlete@technofest.ru')
  AND password_hash = '$2a$10$7Z8V4F0YI.gT9J3S/O0/1.e5pQO4uW6sZ1bM9T8vK7bL5yM3y5s6O';

UPDATE users SET email = lower(btrim(email));
CREATE UNIQUE INDEX uk_users_email_normalized ON users (lower(email));
ALTER TABLE users ALTER COLUMN created_at SET NOT NULL;
ALTER TABLE users ADD CONSTRAINT ck_users_role CHECK (role IN ('ATHLETE', 'ORGANIZER'));

ALTER TABLE competitions ALTER COLUMN venue TYPE VARCHAR(500);
ALTER TABLE competitions ALTER COLUMN description SET NOT NULL;
ALTER TABLE competitions ADD CONSTRAINT ck_competitions_dates CHECK (
    starts_at < ends_at AND registration_opens_at < registration_closes_at
        AND registration_closes_at <= starts_at
    );
ALTER TABLE competitions ADD CONSTRAINT ck_competitions_status CHECK (
    status IN ('UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED')
    );

ALTER TABLE registrations ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE registrations ALTER COLUMN registered_at SET NOT NULL;
ALTER TABLE registrations ADD CONSTRAINT ck_registrations_status CHECK (
    status IN ('REGISTERED', 'CANCELLED')
    );

ALTER TABLE results ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE results ADD COLUMN formula_version INTEGER;
ALTER TABLE results ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE results ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE results ALTER COLUMN performance_value TYPE NUMERIC(19,4);
ALTER TABLE results ALTER COLUMN performance_unit TYPE VARCHAR(30);
ALTER TABLE results ALTER COLUMN rating_points TYPE BIGINT;
ALTER TABLE results ADD CONSTRAINT ck_results_place CHECK (place IS NULL OR place > 0);
ALTER TABLE results ADD CONSTRAINT ck_results_performance CHECK (
    (performance_value IS NULL AND performance_unit IS NULL)
        OR (performance_value IS NOT NULL AND performance_unit IS NOT NULL
        AND length(btrim(performance_unit)) > 0)
    );
ALTER TABLE results ADD CONSTRAINT ck_results_value CHECK (
    place IS NOT NULL OR performance_value IS NOT NULL
    );
ALTER TABLE results ADD CONSTRAINT ck_results_publication CHECK (
    (status = 'DRAFT' AND published_at IS NULL
        AND rating_points IS NULL AND formula_version IS NULL)
        OR (status = 'PUBLISHED' AND published_at IS NOT NULL
        AND rating_points IS NOT NULL AND rating_points >= 0
        AND formula_version IS NOT NULL AND formula_version > 0)
    );

ALTER TABLE qualifications ADD CONSTRAINT ck_qualifications_bonus CHECK (rating_bonus >= 0);
ALTER TABLE athlete_profiles ALTER COLUMN created_at SET NOT NULL;
ALTER TABLE athlete_profiles ALTER COLUMN updated_at SET NOT NULL;
ALTER TABLE rating_snapshots ALTER COLUMN calculated_at SET NOT NULL;
ALTER TABLE rating_snapshots ALTER COLUMN formula_version SET NOT NULL;
ALTER TABLE rating_snapshots ADD CONSTRAINT ck_rating_values CHECK (
    result_points >= 0 AND qualification_points >= 0 AND formula_version > 0
        AND total_points::numeric = result_points::numeric + qualification_points::numeric
    );

CREATE INDEX idx_competitions_status_start ON competitions(status, starts_at, id);
CREATE INDEX idx_competitions_discipline_start ON competitions(discipline_id, starts_at, id);
CREATE INDEX idx_competitions_owner ON competitions(created_by_user_id);
CREATE INDEX idx_registrations_athlete_date ON registrations(athlete_id, registered_at, id);
CREATE INDEX idx_registrations_comp_status ON registrations(competition_id, status);
CREATE INDEX idx_results_published ON results(status, published_at DESC, id DESC);
CREATE INDEX idx_rating_athlete_latest ON rating_snapshots(athlete_id, id DESC);