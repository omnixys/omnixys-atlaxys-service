CREATE TABLE IF NOT EXISTS atlaxys.city (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    state_id    UUID NOT NULL REFERENCES atlaxys.state(id) ON DELETE CASCADE,
    name        TEXT NOT NULL,
    location    GEOGRAPHY(Point, 4326),
    population  BIGINT,
    timezone_id UUID REFERENCES atlaxys.timezone(id) ON DELETE SET NULL,
    type        VARCHAR(50),
    level       INTEGER,
    parent_id   UUID
    REFERENCES atlaxys.city(id) ON DELETE SET NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_city_population_not_null ON atlaxys.city (population DESC) WHERE population IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_city_location ON atlaxys.city USING GIST (location);
CREATE INDEX IF NOT EXISTS idx_city_name_trgm ON atlaxys.city USING GIN (name gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_city_parent ON atlaxys.city(parent_id);
CREATE INDEX IF NOT EXISTS idx_city_timezone ON atlaxys.city (timezone_id);
CREATE UNIQUE INDEX IF NOT EXISTS uq_city_state_name_ci ON atlaxys.city (state_id, lower(name));
CREATE INDEX IF NOT EXISTS idx_city_state ON atlaxys.city (state_id);


ALTER TABLE atlaxys.country ADD COLUMN IF NOT EXISTS capital_city_id UUID REFERENCES atlaxys.city(id) ON DELETE SET NULL;

CREATE TABLE IF NOT EXISTS atlaxys.postal_code (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    country_id  UUID NOT NULL REFERENCES atlaxys.country(id) ON DELETE CASCADE,
    city_id     UUID NOT NULL REFERENCES atlaxys.city(id) ON DELETE CASCADE,
    zip         VARCHAR(20) NOT NULL,
    location    GEOGRAPHY(Point, 4326),
    accuracy    INTEGER,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_postal UNIQUE (country_id, city_id, zip)
    );

CREATE INDEX IF NOT EXISTS idx_postal_zip_country ON atlaxys.postal_code (country_id, zip);
CREATE INDEX IF NOT EXISTS idx_postal_city ON atlaxys.postal_code (city_id);
CREATE INDEX IF NOT EXISTS idx_postal_country ON atlaxys.postal_code (country_id);
CREATE INDEX IF NOT EXISTS idx_postal_location ON atlaxys.postal_code USING GIST (location);
CREATE INDEX IF NOT EXISTS idx_postal_zip_trgm ON atlaxys.postal_code USING GIN (zip gin_trgm_ops);