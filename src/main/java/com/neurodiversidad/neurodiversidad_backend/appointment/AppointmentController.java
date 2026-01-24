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
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.neurodiversidad.neurodiversidad_backend.staff.AssistantRepository;
import com.neurodiversidad.neurodiversidad_backend.staff.SpecialistRepository;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final SpecialistRepository specialistRepository;
    private final AssistantRepository assistantRepository;

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
    public ResponseEntity<org.springframework.data.domain.Page<AppointmentDto>> getAppointments(
            @RequestParam(required = false) UUID specialistId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        OffsetDateTime resolvedFrom = from;
        OffsetDateTime resolvedTo = to;
        if (resolvedFrom == null || resolvedTo == null) {
            ZoneId zone = ZoneId.systemDefault();
            OffsetDateTime startOfDay = LocalDate.now(zone).atStartOfDay(zone).toOffsetDateTime();
            OffsetDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
            resolvedFrom = resolvedFrom != null ? resolvedFrom : startOfDay;
            resolvedTo = resolvedTo != null ? resolvedTo : endOfDay;
        }

        List<UUID> allowedSpecialistIds = resolveAllowedSpecialists(currentUser, specialistId);
        if (allowedSpecialistIds != null && allowedSpecialistIds.isEmpty()) {
            return ResponseEntity.ok(org.springframework.data.domain.Page.empty());
        }

        var result = appointmentService.searchAppointments(
                resolvedFrom,
                resolvedTo,
                status,
                search,
                allowedSpecialistIds,
                page,
                size
        );
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

    private List<UUID> resolveAllowedSpecialists(CustomUserDetails currentUser, UUID requestedSpecialistId) {
        if (currentUser == null) {
            return List.of();
        }

        boolean isDirector = currentUser.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_DIRECTOR_GENERAL"));
        boolean isAssistantGeneral = currentUser.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ASISTENTE_GENERAL"));
        boolean isSpecialist = currentUser.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ESPECIALISTA"));
        boolean isAssistantSpecialist = currentUser.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ASISTENTE_ESPECIALISTA"));

        if (isDirector || isAssistantGeneral) {
            return requestedSpecialistId == null ? null : List.of(requestedSpecialistId);
        }

        if (isSpecialist) {
            UUID specialistId = specialistRepository.findByUserId(currentUser.getId())
                    .map(s -> s.getId())
                    .orElse(null);
            if (specialistId == null) {
                return List.of();
            }
            if (requestedSpecialistId != null && !requestedSpecialistId.equals(specialistId)) {
                return List.of();
            }
            return List.of(specialistId);
        }

        if (isAssistantSpecialist) {
            var assistant = assistantRepository.findByUserId(currentUser.getId()).orElse(null);
            if (assistant == null || assistant.getSpecialists() == null) {
                return List.of();
            }
            List<UUID> ids = assistant.getSpecialists().stream().map(s -> s.getId()).toList();
            if (requestedSpecialistId == null) {
                return ids;
            }
            return ids.contains(requestedSpecialistId) ? List.of(requestedSpecialistId) : List.of();
        }

        return List.of();
    }
}
