package com.neurodiversidad.neurodiversidad_backend.appointment;

import com.neurodiversidad.neurodiversidad_backend.patient.Patient;
import com.neurodiversidad.neurodiversidad_backend.patient.PatientRepository;
import com.neurodiversidad.neurodiversidad_backend.patient.PatientNotFoundException;
import com.neurodiversidad.neurodiversidad_backend.staff.Specialist;
import com.neurodiversidad.neurodiversidad_backend.staff.SpecialistRepository;
import com.neurodiversidad.neurodiversidad_backend.util.SortUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Locale;

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

	    // 5) specialistId (solo si viene)
	    if (request.getSpecialistId() != null) {
	        Specialist specialist = specialistRepository.findById(request.getSpecialistId())
	                .orElseThrow(() -> new IllegalArgumentException(
	                        "Especialista no encontrado con id: " + request.getSpecialistId()));
	        appointment.setSpecialist(specialist);
	    }

	    // 6) metadatos
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
    public Page<AppointmentDto> searchAppointments(OffsetDateTime from, OffsetDateTime to, String status,
            String search, List<UUID> specialistIds, List<String> sort, int page, int size) {

        if (from == null || to == null) {
            throw new IllegalArgumentException("Rango de fechas requerido");
        }

        String normalizedStatus = (status == null || status.isBlank()) ? null : status.toUpperCase();
        if (normalizedStatus != null) {
            switch (normalizedStatus) {
                case "PENDING", "CONFIRMED", "COMPLETED", "CANCELED" -> { }
                default -> throw new IllegalArgumentException("Estado de cita invalido: " + status);
            }
        }

        String normalizedSearch = (search == null || search.isBlank()) ? null : search;

        List<UUID> safeSpecialistIds = (specialistIds == null) ? List.of(new UUID(0L, 0L)) : specialistIds;
        boolean filterBySpecialist = specialistIds != null;

        Sort defaultSort = Sort.by(Sort.Order.asc("startAt"));
        Sort sortSpec = SortUtils.parseSort(
                sort,
                Set.of("startAt", "durationMinutes", "status", "createdAt", "updatedAt"),
                defaultSort
        );
        Sort.Direction statusDirection = resolveStatusSortDirection(sort);

        Page<Appointment> results;
        if (normalizedSearch == null) {
            if (statusDirection != null) {
                results = statusDirection == Sort.Direction.DESC
                        ? appointmentRepository.searchAppointmentsStatusOrderDescWithoutSearch(
                                from,
                                to,
                                normalizedStatus,
                                safeSpecialistIds,
                                filterBySpecialist,
                                PageRequest.of(page, size)
                        )
                        : appointmentRepository.searchAppointmentsStatusOrderAscWithoutSearch(
                                from,
                                to,
                                normalizedStatus,
                                safeSpecialistIds,
                                filterBySpecialist,
                                PageRequest.of(page, size)
                        );
            } else {
                results = appointmentRepository.searchAppointmentsWithoutSearch(
                        from,
                        to,
                        normalizedStatus,
                        safeSpecialistIds,
                        filterBySpecialist,
                        PageRequest.of(page, size, sortSpec)
                );
            }
        } else {
            if (statusDirection != null) {
                results = statusDirection == Sort.Direction.DESC
                        ? appointmentRepository.searchAppointmentsStatusOrderDesc(
                                from,
                                to,
                                normalizedStatus,
                                normalizedSearch,
                                safeSpecialistIds,
                                filterBySpecialist,
                                PageRequest.of(page, size)
                        )
                        : appointmentRepository.searchAppointmentsStatusOrderAsc(
                                from,
                                to,
                                normalizedStatus,
                                normalizedSearch,
                                safeSpecialistIds,
                                filterBySpecialist,
                                PageRequest.of(page, size)
                        );
            } else {
                results = appointmentRepository.searchAppointments(
                        from,
                        to,
                        normalizedStatus,
                        normalizedSearch,
                        safeSpecialistIds,
                        filterBySpecialist,
                        PageRequest.of(page, size, sortSpec)
                );
            }
        }

        return results.map(appointmentMapper::toDto);
    }

    private Sort.Direction resolveStatusSortDirection(List<String> sortParams) {
        if (sortParams == null || sortParams.isEmpty()) {
            return null;
        }
        for (int i = 0; i < sortParams.size(); i++) {
            String raw = sortParams.get(i);
            if (raw == null || raw.isBlank()) {
                continue;
            }
            String[] parts = raw.split(",");
            String field = parts[0].trim();
            if (!"status".equals(field)) {
                continue;
            }
            if (parts.length < 2) {
                // Support legacy pattern: sort=status&sort=asc|desc
                for (int j = i + 1; j < sortParams.size(); j++) {
                    String next = sortParams.get(j);
                    if (next == null || next.isBlank()) {
                        continue;
                    }
                    String nextTrimmed = next.trim().toLowerCase(Locale.ROOT);
                    if ("desc".equals(nextTrimmed)) {
                        return Sort.Direction.DESC;
                    }
                    if ("asc".equals(nextTrimmed)) {
                        return Sort.Direction.ASC;
                    }
                    break;
                }
                return Sort.Direction.ASC;
            }
            String dir = parts[1].trim().toLowerCase(Locale.ROOT);
            if ("desc".equals(dir)) {
                return Sort.Direction.DESC;
            }
            return Sort.Direction.ASC;
        }
        return null;
    }
}
