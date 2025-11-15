package com.neurodiversidad.neurodiversidad_backend.appointment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

	List<Appointment> findBySpecialistIdAndStartAtBetweenAndDeletedAtIsNull(UUID specialistId, OffsetDateTime from,
			OffsetDateTime to);
}
