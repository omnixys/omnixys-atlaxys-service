-- address: actor_id stores UUID identity values (U for users, subject UUID for services).
-- Values are (re)seeded as UUIDs after the UUIDv7 migration, plain casts are safe.
ALTER TABLE analytics_outbox
    ALTER COLUMN actor_id TYPE UUID USING actor_id::uuid;