-- V1__init.sql
-- Esquema base para: usuarios/roles, especialistas/asistentes, pacientes, citas,
-- finanzas, reclutamiento (con historial) y auditoría.

-- 0) Extensiones
CREATE EXTENSION IF NOT EXISTS pgcrypto; -- para gen_random_uuid()

-- 1) Usuarios y Roles
CREATE TABLE users (
  id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  name            text NOT NULL,
  email           text NOT NULL,
  username        text NOT NULL,
  password_hash   text NOT NULL,
  enabled         boolean NOT NULL DEFAULT true,
  last_login_at   timestamptz,
  created_at      timestamptz NOT NULL DEFAULT now(),
  created_by      uuid,
  updated_at      timestamptz,
  updated_by      uuid,
  deleted_at      timestamptz
);

-- unicidad "activos": permite reusar email/username si el registro está borrado (deleted_at IS NOT NULL)
CREATE UNIQUE INDEX ux_users_email_active
  ON users (lower(email))
  WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX ux_users_username_active
  ON users (lower(username))
  WHERE deleted_at IS NULL;

CREATE INDEX ix_users_enabled ON users(enabled);
CREATE INDEX ix_users_deleted_at ON users(deleted_at);

CREATE TABLE roles (
  id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  name        text NOT NULL UNIQUE, -- ROLE_DIRECTOR_GENERAL, etc.
  description text
);

CREATE TABLE user_roles (
  user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  role_id uuid NOT NULL REFERENCES roles(id) ON DELETE RESTRICT,
  PRIMARY KEY (user_id, role_id)
);

-- 2) Especialistas / Asistentes
CREATE TABLE specialists (
  id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id     uuid NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
  specialty   text NOT NULL CHECK (specialty IN ('PSICOLOGIA','PEDAGOGIA','FISIOTERAPIA','PEDIATRIA','OTRA'))
);

CREATE TABLE assistants (
  id             uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id        uuid NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
  specialist_id  uuid NOT NULL REFERENCES specialists(id) ON DELETE CASCADE
);

CREATE INDEX ix_assistants_specialist ON assistants(specialist_id);

-- 3) Pacientes
CREATE TABLE patients (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  full_name     text NOT NULL,
  birth_date    date,
  phone         text,
  email         text,
  notes         text,
  created_at    timestamptz NOT NULL DEFAULT now(),
  created_by    uuid REFERENCES users(id) ON DELETE SET NULL,
  updated_at    timestamptz,
  updated_by    uuid REFERENCES users(id) ON DELETE SET NULL,
  deleted_at    timestamptz
);

CREATE INDEX ix_patients_name ON patients (lower(full_name));
CREATE INDEX ix_patients_deleted_at ON patients(deleted_at);

-- 4) Citas
CREATE TABLE appointments (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  specialist_id uuid NOT NULL REFERENCES specialists(id) ON DELETE RESTRICT,
  patient_id    uuid NOT NULL REFERENCES patients(id) ON DELETE RESTRICT,
  start_at      timestamptz NOT NULL,
  end_at        timestamptz NOT NULL,
  status        text NOT NULL CHECK (status IN ('PENDING','CONFIRMED','COMPLETED','CANCELED')),
  notes         text,
  created_at    timestamptz NOT NULL DEFAULT now(),
  created_by    uuid REFERENCES users(id) ON DELETE SET NULL,
  updated_at    timestamptz,
  updated_by    uuid REFERENCES users(id) ON DELETE SET NULL,
  deleted_at    timestamptz
);

-- índices para búsquedas comunes
CREATE INDEX ix_appt_specialist_start ON appointments (specialist_id, start_at);
CREATE INDEX ix_appt_patient_start    ON appointments (patient_id, start_at);
CREATE INDEX ix_appt_status_start     ON appointments (status, start_at);
CREATE INDEX ix_appt_deleted_at       ON appointments (deleted_at);

-- Evitar solapamientos por especialista (simple: misma hora exacta).
-- Para solapamiento real por rango, implementar regla en servicio o constraint EXCLUDE con gist (requiere int4range/tstzrange).
-- Ejemplo EXCLUDE opcional:
-- CREATE EXTENSION IF NOT EXISTS btree_gist;
-- ALTER TABLE appointments
--   ADD CONSTRAINT appt_no_overlap
--   EXCLUDE USING gist (specialist_id WITH =, tstzrange(start_at, end_at) WITH &&)
--   WHERE (deleted_at IS NULL);

-- 5) Finanzas (entradas/salidas)
CREATE TABLE fin_movements (
  id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  type            text NOT NULL CHECK (type IN ('INCOME','OUTCOME')),
  description     text NOT NULL,
  amount          numeric(12,2) NOT NULL CHECK (amount >= 0),
  movement_date   date NOT NULL,
  payment_method  text NOT NULL CHECK (payment_method IN ('CASH','CARD','TRANSFER','OTHER')),
  created_at      timestamptz NOT NULL DEFAULT now(),
  created_by      uuid REFERENCES users(id) ON DELETE SET NULL,
  updated_at      timestamptz,
  updated_by      uuid REFERENCES users(id) ON DELETE SET NULL,
  deleted_at      timestamptz
);

CREATE INDEX ix_fin_date ON fin_movements(movement_date);
CREATE INDEX ix_fin_type_date ON fin_movements(type, movement_date);
CREATE INDEX ix_fin_deleted_at ON fin_movements(deleted_at);

-- 6) Reclutamiento (perfil + compromisos/historial)
CREATE TABLE recruit_profiles (
  id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  full_name   text NOT NULL,
  phone       text,
  email       text,
  notes       text,
  created_at  timestamptz NOT NULL DEFAULT now(),
  created_by  uuid REFERENCES users(id) ON DELETE SET NULL,
  updated_at  timestamptz,
  updated_by  uuid REFERENCES users(id) ON DELETE SET NULL,
  deleted_at  timestamptz
);

CREATE INDEX ix_recruit_profiles_name ON recruit_profiles(lower(full_name));
CREATE INDEX ix_recruit_profiles_deleted_at ON recruit_profiles(deleted_at);

CREATE TABLE recruit_engagements (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  profile_id    uuid NOT NULL REFERENCES recruit_profiles(id) ON DELETE CASCADE,
  user_id       uuid REFERENCES users(id) ON DELETE SET NULL, -- si luego es usuario del sistema
  service_type  text NOT NULL CHECK (service_type IN ('PRACTICANTE','VOLUNTARIO','SERVICIO_SOCIAL')),
  start_date    date NOT NULL,
  end_date      date,
  status        text NOT NULL CHECK (status IN ('ACTIVO','INACTIVO')),
  created_at    timestamptz NOT NULL DEFAULT now(),
  created_by    uuid REFERENCES users(id) ON DELETE SET NULL,
  updated_at    timestamptz,
  updated_by    uuid REFERENCES users(id) ON DELETE SET NULL,
  deleted_at    timestamptz
);

CREATE INDEX ix_reeng_profile ON recruit_engagements(profile_id, start_date);
CREATE INDEX ix_reeng_status  ON recruit_engagements(status, start_date);
CREATE INDEX ix_reeng_deleted_at ON recruit_engagements(deleted_at);

-- 7) Auditoría global
CREATE TABLE audit_log (
  id             uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  at             timestamptz NOT NULL DEFAULT now(),
  actor_user_id  uuid REFERENCES users(id) ON DELETE SET NULL,
  action         text NOT NULL, -- CREATE|UPDATE|DELETE|LOGIN|...
  entity         text NOT NULL, -- APPOINTMENT|USER|FIN_MOVEMENT|...
  entity_id      uuid,
  payload        jsonb,         -- old/new, detalles, etc.
  ip             text,
  user_agent     text
);

CREATE INDEX ix_audit_entity ON audit_log(entity, entity_id, at);
CREATE INDEX ix_audit_actor  ON audit_log(actor_user_id, at);

-- 8) Roles base (seed opcional)
INSERT INTO roles (id, name, description) VALUES
  (gen_random_uuid(),'ROLE_DIRECTOR_GENERAL','Acceso total'),
  (gen_random_uuid(),'ROLE_ASISTENTE_GENERAL','Todo menos finanzas'),
  (gen_random_uuid(),'ROLE_FINANZAS','Módulo de finanzas'),
  (gen_random_uuid(),'ROLE_ESPECIALISTA','Atiende citas'),
  (gen_random_uuid(),'ROLE_ASISTENTE_ESPECIALISTA','Soporte a especialista'),
  (gen_random_uuid(),'ROLE_TRABAJO_SOCIAL','Área trabajo social'),
  (gen_random_uuid(),'ROLE_CAPACITACION','Comisión de capacitación'),
  (gen_random_uuid(),'ROLE_COMPRAS','Compras'),
  (gen_random_uuid(),'ROLE_RRHH','Recursos Humanos'),
  (gen_random_uuid(),'ROLE_RP','Relaciones públicas');
