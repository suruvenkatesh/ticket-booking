-- V3: an application is created at the Aadhaar OTP step (New Request step 1),
-- so at first we only know the farmer's Aadhaar number.

-- The farmer's 12-digit Aadhaar number.
--   UNIQUE: one application per Aadhaar. The database refuses a second one,
--     and builds an index, so finding an application by Aadhaar is fast.
--   NOT NULL on a new column is safe only because the table is empty.
--     With rows in it, PostgreSQL would refuse: the old rows have no value.
ALTER TABLE farmer_application ADD COLUMN aadhaar_number VARCHAR(12) NOT NULL UNIQUE;

-- These come later, so they may be empty for now:
-- the mobile in New Request step 2, the name in the Personal section.
ALTER TABLE farmer_application ALTER COLUMN farmer_name DROP NOT NULL;
ALTER TABLE farmer_application ALTER COLUMN mobile_number DROP NOT NULL;
