DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_catalog.pg_attribute a
        JOIN pg_catalog.pg_class c ON c.oid = a.attrelid
        JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_catalog.pg_type t ON t.oid = a.atttypid
        WHERE n.nspname = 'public'
          AND c.relname = 'fin_movements'
          AND a.attname = 'description'
          AND t.typname = 'bytea'
    ) THEN
        ALTER TABLE public.fin_movements
        ALTER COLUMN description
        TYPE text
        USING convert_from(description, 'UTF8');
    END IF;
END $$;
