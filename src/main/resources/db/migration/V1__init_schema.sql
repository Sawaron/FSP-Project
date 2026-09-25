CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL,
                       created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE organizations (
                               id BIGSERIAL PRIMARY KEY,
                               name VARCHAR(255) NOT NULL,
                               city VARCHAR(255) NOT NULL
);

CREATE TABLE qualifications (
                                id BIGSERIAL PRIMARY KEY,
                                name VARCHAR(255) UNIQUE NOT NULL,
                                rating_bonus INT NOT NULL,
                                sort_order INT NOT NULL
);

CREATE TABLE disciplines (
                             id BIGSERIAL PRIMARY KEY,
                             name VARCHAR(255) UNIQUE NOT NULL,
                             description TEXT
);

CREATE TABLE athlete_profiles (
                                  id BIGSERIAL PRIMARY KEY,
                                  user_id BIGINT UNIQUE NOT NULL REFERENCES users(id),
                                  full_name VARCHAR(255) NOT NULL,
                                  organization_id BIGINT REFERENCES organizations(id),
                                  city VARCHAR(255) NOT NULL,
                                  qualification_id BIGINT REFERENCES qualifications(id),
                                  created_at TIMESTAMPTZ DEFAULT NOW(),
                                  updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE athlete_disciplines (
                                     athlete_id BIGINT REFERENCES athlete_profiles(id),
                                     discipline_id BIGINT REFERENCES disciplines(id),
                                     PRIMARY KEY (athlete_id, discipline_id)
);

CREATE TABLE competitions (
                              id BIGSERIAL PRIMARY KEY,
                              title VARCHAR(255) NOT NULL,
                              level VARCHAR(50) NOT NULL,
                              discipline_id BIGINT NOT NULL REFERENCES disciplines(id),
                              starts_at TIMESTAMPTZ NOT NULL,
                              ends_at TIMESTAMPTZ NOT NULL,
                              format VARCHAR(50) NOT NULL DEFAULT 'OFFLINE',
                              venue VARCHAR(255),
                              description TEXT,
                              status VARCHAR(50) NOT NULL,
                              registration_opens_at TIMESTAMPTZ NOT NULL,
                              registration_closes_at TIMESTAMPTZ NOT NULL,
                              created_by_user_id BIGINT NOT NULL REFERENCES users(id),
                              version BIGINT NOT NULL DEFAULT 0,
                              created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                              updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE registrations (
                               id BIGSERIAL PRIMARY KEY,
                               competition_id BIGINT NOT NULL REFERENCES competitions(id),
                               athlete_id BIGINT NOT NULL REFERENCES athlete_profiles(id),
                               status VARCHAR(50) NOT NULL,
                               registered_at TIMESTAMPTZ DEFAULT NOW(),
                               UNIQUE(competition_id, athlete_id)
);

CREATE TABLE results (
                         id BIGSERIAL PRIMARY KEY,
                         registration_id BIGINT UNIQUE NOT NULL REFERENCES registrations(id),
                         place INT,
                         performance_value NUMERIC(10,2),
                         performance_unit VARCHAR(50),
                         rating_points INT,
                         status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
                         published_at TIMESTAMPTZ,
                         entered_by_user_id BIGINT NOT NULL REFERENCES users(id)
);

CREATE TABLE rating_snapshots (
                                  id BIGSERIAL PRIMARY KEY,
                                  athlete_id BIGINT NOT NULL REFERENCES athlete_profiles(id),
                                  total_points BIGINT NOT NULL,
                                  result_points BIGINT NOT NULL,
                                  qualification_points BIGINT NOT NULL,
                                  calculated_at TIMESTAMPTZ DEFAULT NOW(),
                                  formula_version INT DEFAULT 1
);