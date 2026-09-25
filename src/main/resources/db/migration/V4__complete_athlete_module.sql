ALTER TABLE athlete_profiles ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE athlete_profiles ADD CONSTRAINT ck_athlete_profiles_text CHECK (
    length(btrim(full_name)) > 0 AND length(btrim(city)) > 0
    );

ALTER TABLE organizations ADD CONSTRAINT ck_organizations_text CHECK (
    length(btrim(name)) > 0 AND length(btrim(city)) > 0
    );

ALTER TABLE qualifications ADD CONSTRAINT ck_qualifications_text CHECK (
    length(btrim(name)) > 0 AND sort_order > 0
    );

ALTER TABLE disciplines ADD CONSTRAINT ck_disciplines_name CHECK (
    length(btrim(name)) > 0
    );

CREATE INDEX idx_athlete_profiles_organization
    ON athlete_profiles(organization_id);

CREATE INDEX idx_athlete_profiles_qualification
    ON athlete_profiles(qualification_id);