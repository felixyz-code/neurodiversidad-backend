package com.neurodiversidad.neurodiversidad_backend.appointment;

import com.neurodiversidad.neurodiversidad_backend.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    /**
     * Crear una nueva cita.
     * POST /api/v1/appointments
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'ESPECIALISTA', 'ASISTENTE_ESPECIALISTA')")
    public ResponseEntity<AppointmentDto> createAppointment(
            @Valid @RequestBody CreateAppointmentRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        UUID currentUserId = currentUser != null ? currentUser.getId() : null;

        AppointmentDto created = appointmentService.createAppointment(request, currentUserId);

        URI location = URI.create(String.format("/api/v1/appointments/%s", created.getId()));
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Obtener cita por id.
     * GET /api/v1/appointments/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'ESPECIALISTA', 'ASISTENTE_ESPECIALISTA', 'TRABAJO_SOCIAL')")
    public ResponseEntity<AppointmentDto> getAppointmentById(@PathVariable UUID id) {
        AppointmentDto dto = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * Listar citas de un especialista en un rango de fechas.
     * GET /api/v1/appointments?specialistId=...&from=...&to=...
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'ESPECIALISTA', 'ASISTENTE_ESPECIALISTA')")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsForSpecialist(
            @RequestParam UUID specialistId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to) {

        List<AppointmentDto> result = appointmentService.getAppointmentsForSpecialist(specialistId, from, to);
        return ResponseEntity.ok(result);
    }

    /**
     * Actualizar una cita.
     * PUT /api/v1/appointments/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'ESPECIALISTA', 'ASISTENTE_ESPECIALISTA')")
    public ResponseEntity<AppointmentDto> updateAppointment(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAppointmentRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        UUID currentUserId = currentUser != null ? currentUser.getId() : null;

        AppointmentDto updated = appointmentService.updateAppointment(id, request, currentUserId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Cancelar (soft delete) una cita.
     * DELETE /api/v1/appointments/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL')")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        UUID currentUserId = currentUser != null ? currentUser.getId() : null;

        appointmentService.cancelAppointment(id, currentUserId);
        return ResponseEntity.noContent().build();
    }
}
