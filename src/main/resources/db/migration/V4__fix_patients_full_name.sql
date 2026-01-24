-- Fix patients.full_name if it was created as bytea in a previous schema.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'patients'
          AND column_name = 'full_name'
          AND data_type = 'bytea'
    ) THEN
        ALTER TABLE patients
            ALTER COLUMN full_name TYPE text USING convert_from(full_name, 'UTF8');
    END IF;
END $$;
