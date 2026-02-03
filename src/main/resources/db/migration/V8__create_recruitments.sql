CREATE TABLE IF NOT EXISTS recruitments (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  nombre text NOT NULL,
  tipo_servicio text NOT NULL,
  fecha_inicio date NOT NULL,
  fecha_salida date,
  estatus text NOT NULL,
  created_at timestamptz NOT NULL,
  created_by uuid,
  updated_at timestamptz,
  updated_by uuid,
  deleted_at timestamptz
);

CREATE INDEX IF NOT EXISTS ix_recruitments_fecha_inicio ON recruitments(fecha_inicio);
CREATE INDEX IF NOT EXISTS ix_recruitments_deleted_at ON recruitments(deleted_at);
CREATE INDEX IF NOT EXISTS ix_recruitments_tipo_servicio ON recruitments(tipo_servicio);
CREATE INDEX IF NOT EXISTS ix_recruitments_estatus ON recruitments(estatus);
