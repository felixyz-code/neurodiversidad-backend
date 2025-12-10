-- Primero eliminamos la FK incorrecta que apunta a users
ALTER TABLE appointments
    DROP CONSTRAINT fk_appointments_specialist;

-- Luego creamos la FK correcta que apunte a specialists(id)
ALTER TABLE appointments
    ADD CONSTRAINT fk_appointments_specialist
    FOREIGN KEY (specialist_id)
    REFERENCES specialists (id);
