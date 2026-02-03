package com.neurodiversidad.neurodiversidad_backend.recruitment;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecruitmentDto {
	private UUID id;
	private String nombre;
	private RecruitmentServiceType tipoServicio;
	private LocalDate fechaInicio;
	private LocalDate fechaSalida;
	private RecruitmentStatus estatus;
	private OffsetDateTime createdAt;
	private UUID createdBy;
	private OffsetDateTime updatedAt;
	private UUID updatedBy;
	private OffsetDateTime deletedAt;
}
