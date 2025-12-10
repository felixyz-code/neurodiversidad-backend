-- Si quieres conservar datos viejos, primero podrías copiar:
-- UPDATE appointments SET start_at = appointment_date WHERE start_at IS NULL;

ALTER TABLE appointments
    DROP COLUMN appointment_date;
