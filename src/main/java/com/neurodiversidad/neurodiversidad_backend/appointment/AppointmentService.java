package com.neurodiversidad.neurodiversidad_backend.appointment;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentService {

    AppointmentDto createAppointment(CreateAppointmentRequest request, UUID currentUserId);

    AppointmentDto updateAppointment(UUID id, UpdateAppointmentRequest request, UUID currentUserId);

    void cancelAppointment(UUID id, UUID currentUserId);

    AppointmentDto getAppointmentById(UUID id);

    List<AppointmentDto> getAppointmentsForSpecialist(
            UUID specialistId,
            OffsetDateTime from,
            OffsetDateTime to
    );
}
