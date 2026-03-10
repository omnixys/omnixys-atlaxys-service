CREATE TYPE address.address_type AS ENUM (
    'HOME',
    'WORK',
    'SHIPPING',
    'BILLING'
    );

-- =========================
-- STREET
-- =========================

CREATE TABLE IF NOT EXISTS address.user_address (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID NOT NULL,

    country_id      UUID NOT NULL
    REFERENCES address.country(id)
        ON DELETE RESTRICT,

    state_id      UUID
    REFERENCES address.state(id)
        ON DELETE SET NULL,

    city_id      UUID NOT NULL
    REFERENCES address.city(id)
        ON DELETE RESTRICT,

    postal_code_id      UUID
    REFERENCES address.postal_code(id)
        ON DELETE SET NULL,

    street_id      UUID NOT NULL
    REFERENCES address.street(id)
        ON DELETE RESTRICT,

    house_number_id      UUID NOT NULL
    REFERENCES address.house_number(id)
        ON DELETE RESTRICT,

    additional_info VARCHAR(50),
    address_type address.address_type NOT NULL,

    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- =========================
-- INDEXES
-- =========================

CREATE INDEX IF NOT EXISTS idx_user_address_user
    ON address.user_address (user_id);

CREATE INDEX IF NOT EXISTS idx_user_address_city
    ON address.user_address (city_id);

CREATE INDEX IF NOT EXISTS idx_user_address_postal
    ON address.user_address (postal_code_id);

CREATE INDEX IF NOT EXISTS idx_user_address_country
    ON address.user_address (country_id);

CREATE UNIQUE INDEX uq_user_home_address
    ON address.user_address(user_id)
    WHERE address_type = 'HOME';