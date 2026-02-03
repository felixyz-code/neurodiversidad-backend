DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'fin_movements'
          AND column_name = 'description'
          AND data_type = 'bytea'
    ) THEN
        ALTER TABLE fin_movements
        ALTER COLUMN description
        TYPE text
        USING convert_from(description, 'UTF8');
    END IF;
END $$;
