-- V1: the main table for farmer onboarding applications.
-- Flyway runs this file once per database, then records it in flyway_schema_history.
-- Never edit this file after it has run anywhere: add a V2__... file instead.

CREATE TABLE farmer_application (
    -- The unique id of each application. PRIMARY KEY = unique and never empty.
    id            UUID                     PRIMARY KEY,

    -- Shown on the worklist cards. NOT NULL = this column must always have a value.
    farmer_name   VARCHAR(100)             NOT NULL,
    -- Text, not a number: it's an identifier, nobody does maths with it. 10 digits in India.
    mobile_number VARCHAR(10)              NOT NULL,

    -- The workflow status as text, like DRAFT or SUBMITTED.
    status        VARCHAR(30)              NOT NULL,

    -- The exact moment the application was created, with its time zone.
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL
);
