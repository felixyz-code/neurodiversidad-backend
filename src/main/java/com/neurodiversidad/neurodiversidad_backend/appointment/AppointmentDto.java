package com.neurodiversidad.neurodiversidad_backend.appointment;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class AppointmentDto {

	private UUID id;

	private UUID specialistId;

	private String specialistName;

	private UUID patientId;

	private String patientName;

	private OffsetDateTime startAt;
	
	private OffsetDateTime endAt;
	
	private Integer durationMinutes;

	private String status;

	private String notes;

	private OffsetDateTime createdAt;

	private UUID createdBy;

	private OffsetDateTime updatedAt;

	private UUID updatedBy;
}
