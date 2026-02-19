CREATE SCHEMA IF NOT EXISTS "atlaxys" AUTHORIZATION "atlaxys";
ALTER ROLE "atlaxys" SET search_path = 'atlaxys';

CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- CONTINENT
CREATE TABLE IF NOT EXISTS atlaxys.continent (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    code        VARCHAR(10) UNIQUE, -- optional (EU, AF, NA, ...)
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );
CREATE INDEX idx_continent_code ON atlaxys.continent(code);


-- SUBREGION
CREATE TABLE IF NOT EXISTS atlaxys.subregion (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(150) NOT NULL,
    continent_id  UUID NOT NULL REFERENCES atlaxys.continent(id) ON DELETE RESTRICT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_subregion UNIQUE (name, continent_id)
    );
CREATE INDEX idx_subregion_continent ON atlaxys.subregion(continent_id);


-- CURRENCY
CREATE TABLE IF NOT EXISTS atlaxys.currency (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL,
    code        CHAR(3) NOT NULL UNIQUE, -- ISO 4217
    symbol      VARCHAR(10),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- LANGUAGE
CREATE TABLE IF NOT EXISTS atlaxys.language (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL,
    iso2        CHAR(2) UNIQUE,  -- ISO 639-1
    iso3        CHAR(3) UNIQUE,  -- ISO 639-2
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- TIMEZONE
CREATE TABLE IF NOT EXISTS atlaxys.timezone (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    utc_offset  VARCHAR(20) NOT NULL UNIQUE, -- e.g. UTC+01:00
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- CALLING CODE
CREATE TABLE IF NOT EXISTS atlaxys.calling_code (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(10) NOT NULL UNIQUE, -- e.g. +49
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );


-- Country
CREATE TABLE IF NOT EXISTS atlaxys.country (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(200) NOT NULL,
    iso2          CHAR(2) NOT NULL UNIQUE,
    iso3          CHAR(3) NOT NULL UNIQUE,
    flag_svg      VARCHAR(200) NOT NULL,
    flag_png      VARCHAR(200) NOT NULL,
    native_name VARCHAR(200) NOT NULL,


    continent_id     UUID NOT NULL REFERENCES atlaxys.continent(id) ON DELETE RESTRICT,
    subregion_id     UUID NOT NULL REFERENCES atlaxys.subregion(id) ON DELETE RESTRICT,
    capital_city_id  UUID,

    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX idx_country_continent ON atlaxys.country(continent_id);
CREATE INDEX idx_country_subregion ON atlaxys.country(subregion_id);


CREATE TABLE atlaxys.country_timezone (
    country_id  UUID REFERENCES atlaxys.country(id) ON DELETE CASCADE,
    timezone_id UUID REFERENCES atlaxys.timezone(id) ON DELETE CASCADE,
    PRIMARY KEY (country_id, timezone_id)
);

CREATE TABLE atlaxys.country_language (
    country_id  UUID REFERENCES atlaxys.country(id) ON DELETE CASCADE,
    language_id UUID REFERENCES atlaxys.language(id) ON DELETE CASCADE,
    PRIMARY KEY (country_id, language_id)
);

CREATE TABLE atlaxys.country_currency (
    country_id  UUID REFERENCES atlaxys.country(id) ON DELETE CASCADE,
    currency_id UUID REFERENCES atlaxys.currency(id) ON DELETE CASCADE,
    PRIMARY KEY (country_id, currency_id)
);

CREATE TABLE atlaxys.country_calling_code (
    country_id      UUID REFERENCES atlaxys.country(id) ON DELETE CASCADE,
    calling_code_id UUID REFERENCES atlaxys.calling_code(id) ON DELETE CASCADE,
    PRIMARY KEY (country_id, calling_code_id)
);

-- STATE
CREATE TABLE IF NOT EXISTS atlaxys.state (
                                             id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    country_id  UUID NOT NULL REFERENCES atlaxys.country(id) ON DELETE CASCADE,
    code VARCHAR(20) NOT NULL,
    name        VARCHAR(200) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_state_country_code UNIQUE (country_id, code)
    );
CREATE INDEX IF NOT EXISTS idx_state_country ON atlaxys.state (country_id);
CREATE INDEX IF NOT EXISTS idx_state_name_trgm ON atlaxys.state USING GIN (name gin_trgm_ops);

-- CITY
CREATE TABLE IF NOT EXISTS atlaxys.city (
                                            id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    state_id    UUID NOT NULL REFERENCES atlaxys.state(id) ON DELETE CASCADE,
    name        VARCHAR(200) NOT NULL,
    location    GEOGRAPHY(Point, 4326),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_city UNIQUE (state_id, name)
    );

CREATE INDEX IF NOT EXISTS idx_city_state ON atlaxys.city (state_id);
CREATE INDEX IF NOT EXISTS idx_city_location ON atlaxys.city USING GIST (location);
CREATE INDEX IF NOT EXISTS idx_city_name_trgm ON atlaxys.city USING GIN (name gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_city_name_country ON atlaxys.city (state_id, name);

-- POSTAL CODE
CREATE TABLE IF NOT EXISTS atlaxys.postal_code (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    city_id     UUID NOT NULL REFERENCES atlaxys.city(id) ON DELETE CASCADE,
    zip         VARCHAR(20) NOT NULL,
    location    GEOGRAPHY(Point, 4326),
    accuracy    INTEGER,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_postal UNIQUE (city_id, zip)
    );

CREATE INDEX IF NOT EXISTS idx_postal_zip ON atlaxys.postal_code (zip);
CREATE INDEX IF NOT EXISTS idx_postal_city ON atlaxys.postal_code (city_id);
CREATE INDEX IF NOT EXISTS idx_postal_location ON atlaxys.postal_code USING GIST (location);
CREATE INDEX IF NOT EXISTS idx_postal_zip_trgm ON atlaxys.postal_code USING GIN (zip gin_trgm_ops);
CREATE INDEX idx_postal_zip_state ON atlaxys.postal_code (zip, city_id);



CREATE TABLE atlaxys.staging_postal (
    country_code    VARCHAR(2),
    postal_code     VARCHAR(20),
    place_name      VARCHAR(180),
    admin_name1     VARCHAR(100),
    admin_code1     VARCHAR(20),
    admin_name2     VARCHAR(100),
    admin_code2     VARCHAR(20),
    admin_name3     VARCHAR(100),
    admin_code3     VARCHAR(20),
    latitude        DOUBLE PRECISION,
    longitude       DOUBLE PRECISION,
    accuracy        INTEGER
);

CREATE INDEX IF NOT EXISTS idx_staging_country ON atlaxys.staging_postal (country_code);
CREATE INDEX IF NOT EXISTS idx_staging_zip ON atlaxys.staging_postal (postal_code);



ALTER TABLE atlaxys.country ADD CONSTRAINT fk_country_capital FOREIGN KEY (capital_city_id) REFERENCES atlaxys.city(id) ON DELETE SET NULL;
