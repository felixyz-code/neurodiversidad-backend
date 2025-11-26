-- Eliminar columnas legacy si existen
ALTER TABLE appointments
    DROP COLUMN IF EXISTS appointment_date;

ALTER TABLE appointments
    DROP COLUMN IF EXISTS appointment_time;
