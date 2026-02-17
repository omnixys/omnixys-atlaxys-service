/* ---------------------------------------------------------------------------
   V1__init_schema.sql
   GeoRegistry Service - initial schema (PostgreSQL + PostGIS)
   --------------------------------------------------------------------------- */

-- Recommended: keep everything idempotent where possible for safer dev resets
-- Flyway runs each migration once per database, but IF NOT EXISTS avoids trouble.

-- 1) Schema
CREATE SCHEMA IF NOT EXISTS atlaxys;

-- 2) Extensions
-- Note: requires privileges. In production, you may run these as a DBA once.
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- 3) Country
       SET search_path TO atlaxys, public;
CREATE TABLE IF NOT EXISTS atlaxys.country (
                                           id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(150) NOT NULL,
    iso2        CHAR(2) NOT NULL,
    iso3        CHAR(3) NOT NULL,
    flag_svg    TEXT,
    flag_png    TEXT,
    timezone    VARCHAR(100),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_country_iso2 UNIQUE (iso2),
    CONSTRAINT uq_country_iso3 UNIQUE (iso3)
    );

CREATE INDEX IF NOT EXISTS idx_country_iso2 ON atlaxys.country (iso2);
CREATE INDEX IF NOT EXISTS idx_country_iso3 ON atlaxys.country (iso3);

-- 4) State / Region
CREATE TABLE IF NOT EXISTS atlaxys.state (
                                         id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    country_id  UUID NOT NULL,
    name        VARCHAR(150) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_state_country FOREIGN KEY (country_id)
    REFERENCES atlaxys.country(id) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_state_country ON atlaxys.state (country_id);
CREATE INDEX IF NOT EXISTS idx_state_name_trgm ON atlaxys.state USING GIN (name gin_trgm_ops);

-- 5) City
CREATE TABLE IF NOT EXISTS atlaxys.city (
                                        id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    state_id    UUID NOT NULL,
    name        VARCHAR(150) NOT NULL,
    -- PostGIS atlaxysgraphy type for radius queries in meters
    location    GEOGRAPHY(Point, 4326),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_city_state FOREIGN KEY (state_id)
    REFERENCES atlaxys.state(id) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_city_state ON atlaxys.city (state_id);
CREATE INDEX IF NOT EXISTS idx_city_location ON atlaxys.city USING GIST (location);
CREATE INDEX IF NOT EXISTS idx_city_name_trgm ON atlaxys.city USING GIN (name gin_trgm_ops);

-- 6) Postal Code
CREATE TABLE IF NOT EXISTS atlaxys.postal_code (
                                               id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    city_id     UUID NOT NULL,
    zip         VARCHAR(20) NOT NULL,
    latitude    NUMERIC(9,6),
    longitude   NUMERIC(9,6),
    location    GEOGRAPHY(Point, 4326),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_postal_city FOREIGN KEY (city_id)
    REFERENCES atlaxys.city(id) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_postal_zip ON atlaxys.postal_code (zip);
CREATE INDEX IF NOT EXISTS idx_postal_city ON atlaxys.postal_code (city_id);
CREATE INDEX IF NOT EXISTS idx_postal_location ON atlaxys.postal_code USING GIST (location);
CREATE INDEX IF NOT EXISTS idx_postal_zip_trgm ON atlaxys.postal_code USING GIN (zip gin_trgm_ops);

-- Optional: ensure zip uniqueness per city (varies by country; keep OFF by default)
-- CREATE UNIQUE INDEX IF NOT EXISTS uq_postal_city_zip ON atlaxys.postal_code (city_id, zip);

-- 7) Address (optional baseline)
CREATE TABLE IF NOT EXISTS atlaxys.address (
                                           id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    street          VARCHAR(200),
    house_number    VARCHAR(50),
    postal_code_id  UUID,
    raw_input       TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_address_postal FOREIGN KEY (postal_code_id)
    REFERENCES atlaxys.postal_code(id)
    );

CREATE INDEX IF NOT EXISTS idx_address_postal ON atlaxys.address (postal_code_id);
