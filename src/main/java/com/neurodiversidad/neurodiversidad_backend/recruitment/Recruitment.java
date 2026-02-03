package com.neurodiversidad.neurodiversidad_backend.recruitment;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "recruitments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recruitment {

	@Id
	@GeneratedValue
	@Column(columnDefinition = "uuid")
	private UUID id;

	@Column(name = "nombre", nullable = false)
	private String nombre;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_servicio", nullable = false)
	private RecruitmentServiceType tipoServicio;

	@Column(name = "fecha_inicio", nullable = false)
	private LocalDate fechaInicio;

	@Column(name = "fecha_salida")
	private LocalDate fechaSalida;

	@Enumerated(EnumType.STRING)
	@Column(name = "estatus", nullable = false)
	private RecruitmentStatus estatus;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	@Column(name = "created_by")
	private UUID createdBy;

	@Column(name = "updated_at")
	private OffsetDateTime updatedAt;

	@Column(name = "updated_by")
	private UUID updatedBy;

	@Column(name = "deleted_at")
	private OffsetDateTime deletedAt;
}
