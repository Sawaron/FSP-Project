ALTER TABLE competitions DROP CONSTRAINT ck_competitions_status;
ALTER TABLE competitions ADD CONSTRAINT ck_competitions_status
    CHECK (status IN ('DRAFT', 'UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED'));
ALTER TABLE competitions ADD COLUMN rules VARCHAR(5000) NOT NULL DEFAULT '';
ALTER TABLE competitions ADD COLUMN contest_enabled BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE competitions ADD COLUMN finalized_at TIMESTAMPTZ;
ALTER TABLE competitions ADD CONSTRAINT ck_competitions_finalized
    CHECK (finalized_at IS NULL OR (contest_enabled AND status = 'COMPLETED'));

CREATE TABLE contest_tasks (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    competition_id BIGINT NOT NULL REFERENCES competitions(id),
    title VARCHAR(200) NOT NULL CHECK (length(btrim(title)) > 0),
    statement VARCHAR(20000) NOT NULL CHECK (length(btrim(statement)) > 0),
    max_points INTEGER NOT NULL CHECK (max_points BETWEEN 1 AND 1000000),
    sort_order INTEGER NOT NULL CHECK (sort_order >= 0),
    UNIQUE (id, competition_id)
);

ALTER TABLE registrations ADD CONSTRAINT uk_registration_id_competition UNIQUE (id, competition_id);

CREATE TABLE contest_submissions (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    competition_id BIGINT NOT NULL REFERENCES competitions(id),
    task_id BIGINT NOT NULL,
    registration_id BIGINT NOT NULL,
    answer VARCHAR(50000) NOT NULL CHECK (length(btrim(answer)) > 0),
    submitted_at TIMESTAMPTZ NOT NULL,
    points INTEGER CHECK (points >= 0),
    review_comment VARCHAR(5000),
    reviewed_by BIGINT REFERENCES users(id),
    reviewed_at TIMESTAMPTZ,
    CONSTRAINT uk_submission_task_registration UNIQUE (task_id, registration_id),
    FOREIGN KEY (task_id, competition_id) REFERENCES contest_tasks(id, competition_id),
    FOREIGN KEY (registration_id, competition_id) REFERENCES registrations(id, competition_id),
    CONSTRAINT ck_submission_review CHECK (
        (points IS NULL AND reviewed_by IS NULL AND reviewed_at IS NULL AND review_comment IS NULL)
        OR (points IS NOT NULL AND reviewed_by IS NOT NULL AND reviewed_at IS NOT NULL)
    )
);

CREATE INDEX idx_contest_tasks_order ON contest_tasks(competition_id, sort_order, id);
CREATE INDEX idx_contest_submissions_review ON contest_submissions(competition_id, points, id);
CREATE INDEX idx_contest_submissions_registration ON contest_submissions(registration_id);
