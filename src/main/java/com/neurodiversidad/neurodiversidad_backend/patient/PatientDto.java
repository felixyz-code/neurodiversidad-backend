package com.neurodiversidad.neurodiversidad_backend.patient;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class PatientDto {

	private UUID id;
	private String fullName;
	private LocalDate birthDate;
	private String phone;
	private String email;
	private String notes;
	private OffsetDateTime createdAt;
	private UUID createdBy;
	private OffsetDateTime updatedAt;
	private UUID updatedBy;
}
