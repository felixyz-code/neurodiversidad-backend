-- Convert assistants -> specialists relationship to many-to-many.
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_name = 'assistant_specialists'
    ) THEN
        CREATE TABLE assistant_specialists (
            assistant_id uuid NOT NULL REFERENCES assistants(id) ON DELETE CASCADE,
            specialist_id uuid NOT NULL REFERENCES specialists(id) ON DELETE CASCADE,
            PRIMARY KEY (assistant_id, specialist_id)
        );
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'assistants'
          AND column_name = 'specialist_id'
    ) THEN
        INSERT INTO assistant_specialists (assistant_id, specialist_id)
        SELECT id, specialist_id
        FROM assistants
        WHERE specialist_id IS NOT NULL
        ON CONFLICT DO NOTHING;

        ALTER TABLE assistants
            DROP COLUMN specialist_id;
    END IF;
END $$;
