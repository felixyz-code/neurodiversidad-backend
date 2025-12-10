package com.neurodiversidad.neurodiversidad_backend.appointment;

import com.neurodiversidad.neurodiversidad_backend.patient.Patient;
import com.neurodiversidad.neurodiversidad_backend.patient.PatientRepository;
import com.neurodiversidad.neurodiversidad_backend.patient.PatientNotFoundException;
import com.neurodiversidad.neurodiversidad_backend.staff.Specialist;
import com.neurodiversidad.neurodiversidad_backend.staff.SpecialistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

	private final AppointmentRepository appointmentRepository;
	private final PatientRepository patientRepository;
	private final SpecialistRepository specialistRepository;
	private final AppointmentMapper appointmentMapper;

	@Override
	public AppointmentDto createAppointment(CreateAppointmentRequest request, UUID currentUserId) {

		Patient patient = patientRepository.findById(request.getPatientId()).orElseThrow(
				() -> new PatientNotFoundException("Paciente no encontrado con id: " + request.getPatientId()));

		Specialist specialist = specialistRepository.findById(request.getSpecialistId()).orElseThrow(
				() -> new IllegalArgumentException("Especialista no encontrado con id: " + request.getSpecialistId()));

		String status = (request.getStatus() == null || request.getStatus().isBlank()) ? "PENDING"
				: request.getStatus().toUpperCase();

		Appointment appointment = Appointment.builder().patient(patient).specialist(specialist)
				.startAt(request.getStartAt()).durationMinutes(request.getDurationMinutes()).status(status)
				.notes(request.getNotes()).createdAt(OffsetDateTime.now()).createdBy(currentUserId).build();

		appointment = appointmentRepository.save(appointment);

		return appointmentMapper.toDto(appointment);
	}

	@Override
	public AppointmentDto updateAppointment(UUID id, UpdateAppointmentRequest request, UUID currentUserId) {

	    Appointment appointment = appointmentRepository.findByIdAndDeletedAtIsNull(id)
	            .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada con id: " + id));

	    // 1) startAt (solo si viene)
	    if (request.getStartAt() != null) {
	        appointment.setStartAt(request.getStartAt());
	    }

	    // 2) notes (solo si viene; si quieres permitir "vaciar" notas con "", usa != null en vez de !isBlank)
	    if (request.getNotes() != null) {
	        appointment.setNotes(request.getNotes());
	    }

	    // 3) durationMinutes (solo si viene)
	    if (request.getDurationMinutes() != null) {
	        appointment.setDurationMinutes(request.getDurationMinutes());
	    }

	    // 4) status (solo si viene y no viene vacío)
	    if (request.getStatus() != null && !request.getStatus().isBlank()) {
	        String normalizedStatus = request.getStatus().toUpperCase();
	        switch (normalizedStatus) {
	            case "PENDING", "CONFIRMED", "COMPLETED", "CANCELED" -> {
	                appointment.setStatus(normalizedStatus);
	            }
	            default -> throw new IllegalArgumentException("Estado de cita inválido: " + request.getStatus());
	        }
	    }

	    // 5) metadatos
	    appointment.setUpdatedAt(OffsetDateTime.now());
	    appointment.setUpdatedBy(currentUserId);

	    appointment = appointmentRepository.save(appointment);

	    return appointmentMapper.toDto(appointment);
	}

	@Override
	public void cancelAppointment(UUID id, UUID currentUserId) {
		Appointment appointment = appointmentRepository.findByIdAndDeletedAtIsNull(id)
				.orElseThrow(() -> new IllegalArgumentException("Cita no encontrada con id: " + id));

		appointment.setStatus("CANCELED");
		appointment.setDeletedAt(OffsetDateTime.now());
		appointment.setUpdatedAt(OffsetDateTime.now());
		appointment.setUpdatedBy(currentUserId);

		appointmentRepository.save(appointment);
	}

	@Override
	@Transactional(readOnly = true)
	public AppointmentDto getAppointmentById(UUID id) {
		Appointment appointment = appointmentRepository.findByIdAndDeletedAtIsNull(id)
				.orElseThrow(() -> new IllegalArgumentException("Cita no encontrada con id: " + id));

		return appointmentMapper.toDto(appointment);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AppointmentDto> getAppointmentsForSpecialist(UUID specialistId, OffsetDateTime from,
			OffsetDateTime to) {

		List<Appointment> appointments = appointmentRepository
				.findBySpecialistIdAndStartAtBetweenAndDeletedAtIsNull(specialistId, from, to);

		return appointments.stream().map(appointmentMapper::toDto).toList();
	}
}
