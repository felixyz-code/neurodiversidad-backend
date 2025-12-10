-- 1) Agregar la columna como nullable inicialmente
ALTER TABLE appointments
    ADD COLUMN duration_minutes INTEGER;

-- 2) Dar valor por defecto a las citas existentes
UPDATE appointments
SET duration_minutes = 60
WHERE duration_minutes IS NULL;

-- 3) Hacer la columna NOT NULL (la app asume que siempre hay duración)
ALTER TABLE appointments
    ALTER COLUMN duration_minutes SET NOT NULL;

-- (Opcional pero recomendable) dejar default 60 para inserciones sin valor explícito
ALTER TABLE appointments
    ALTER COLUMN duration_minutes SET DEFAULT 60;
-