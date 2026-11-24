-- V2: switch the id from a UUID to a number the database counts up: 1, 2, 3...
-- V1 already ran and is shared on GitHub, so we never edit it. We change the table here instead.
-- Dropping the column is safe only because the table is still empty and nothing is live yet.

-- Remove the UUID id. Its PRIMARY KEY rule and its index are removed with it.
ALTER TABLE farmer_application DROP COLUMN id;

-- Add the new id:
--   BIGINT: a big whole number (up to about 9 quintillion), so it never runs out.
--   GENERATED ALWAYS AS IDENTITY: PostgreSQL picks the next number itself,
--     and nobody can insert their own.
--   PRIMARY KEY: unique and never empty, with an index for fast lookups.
ALTER TABLE farmer_application ADD COLUMN id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY;
