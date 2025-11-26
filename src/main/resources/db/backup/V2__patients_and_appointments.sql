-- ============================
-- V2: Pacientes y Citas
-- ============================

-- 1) Tabla de pacientes
CREATE TABLE patients (
    id           UUID PRIMARY KEY,
    full_name    VARCHAR(200) NOT NULL,
    phone        VARCHAR(50),
    notes        TEXT,
    created_at   TIMESTAMPTZ,
    created_by   UUID,
    updated_at   TIMESTAMPTZ,
    updated_by   UUID,
    deleted_at   TIMESTAMPTZ,
    deleted_by   UUID
);

-- 2) Tabla de citas
CREATE TABLE appointments (
    id             UUID PRIMARY KEY,
    patient_id     UUID NOT NULL,
    specialist_id  UUID NOT NULL,
    assistant_id   UUID,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status         VARCHAR(20) NOT NULL, -- PENDING, COMPLETED, CANCELLED
    notes          TEXT,
    created_at     TIMESTAMPTZ,
    created_by     UUID,
    updated_at     TIMESTAMPTZ,
    updated_by     UUID,
    deleted_at     TIMESTAMPTZ,
    deleted_by     UUID
);

-- 3) Llaves foráneas
ALTER TABLE appointments
    ADD CONSTRAINT fk_appointments_patient
        FOREIGN KEY (patient_id) REFERENCES patients (id);

ALTER TABLE appointments
    ADD CONSTRAINT fk_appointments_specialist
        FOREIGN KEY (specialist_id) REFERENCES users (id);

ALTER TABLE appointments
    ADD CONSTRAINT fk_appointments_assistant
        FOREIGN KEY (assistant_id) REFERENCES users (id);
