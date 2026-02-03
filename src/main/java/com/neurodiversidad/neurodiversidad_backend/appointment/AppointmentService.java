package com.neurodiversidad.neurodiversidad_backend.appointment;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface AppointmentService {

    AppointmentDto createAppointment(CreateAppointmentRequest request, UUID currentUserId);

    AppointmentDto updateAppointment(UUID id, UpdateAppointmentRequest request, UUID currentUserId);

    void cancelAppointment(UUID id, UUID currentUserId);

    AppointmentDto getAppointmentById(UUID id);

    Page<AppointmentDto> searchAppointments(
            OffsetDateTime from,
            OffsetDateTime to,
            String status,
            String search,
            List<UUID> specialistIds,
            List<String> sort,
            int page,
            int size
    );
}
