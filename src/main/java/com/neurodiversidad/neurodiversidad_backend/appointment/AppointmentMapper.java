package com.neurodiversidad.neurodiversidad_backend.appointment;

import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

	public AppointmentDto toDto(Appointment entity) {
		if (entity == null) {
			return null;
		}

		return AppointmentDto.builder()
				.id(entity.getId()).specialistId(entity.getSpecialist().getId())
				.specialistName(entity.getSpecialist().getUser().getName())
				.patientId(entity.getPatient().getId())
				.patientName(entity.getPatient().getFullName())
				.startAt(entity.getStartAt()).endAt(entity.getEndAt())
				.durationMinutes(entity.getDurationMinutes())
				.status(entity.getStatus())
				.notes(entity.getNotes())
				.createdAt(entity.getCreatedAt())
				.createdBy(entity.getCreatedBy())
				.updatedAt(entity.getUpdatedAt())
				.updatedBy(entity.getUpdatedBy())
				.build();
	}
}
