-- Fix users text columns if they were created as bytea in a previous schema.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'users'
          AND column_name = 'name'
          AND data_type = 'bytea'
    ) THEN
        ALTER TABLE users
            ALTER COLUMN name TYPE text USING convert_from(name, 'UTF8');
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'users'
          AND column_name = 'email'
          AND data_type = 'bytea'
    ) THEN
        ALTER TABLE users
            ALTER COLUMN email TYPE text USING convert_from(email, 'UTF8');
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'users'
          AND column_name = 'username'
          AND data_type = 'bytea'
    ) THEN
        ALTER TABLE users
            ALTER COLUMN username TYPE text USING convert_from(username, 'UTF8');
    END IF;
END $$;
