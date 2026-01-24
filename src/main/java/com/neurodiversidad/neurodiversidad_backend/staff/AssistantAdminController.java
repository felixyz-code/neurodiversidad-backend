package com.neurodiversidad.neurodiversidad_backend.staff;

import com.neurodiversidad.neurodiversidad_backend.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/staff/assistants")
@Tag(name = "Assistants", description = "Administracion de asistentes especialistas")
@RequiredArgsConstructor
public class AssistantAdminController {

    private final AssistantAdminService assistantAdminService;

    /**
     * Crea un asistente especialista y lo relaciona con especialistas.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
    @Operation(summary = "Registra un asistente especialista asociado a especialistas")
    public AssistantDto createAssistant(
            @Valid @RequestBody CreateAssistantRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        UUID currentUserId = currentUser != null ? currentUser.getId() : null;
        return assistantAdminService.createAssistant(request, currentUserId);
    }

    /**
     * Lista asistentes. Si se pasa specialistId, filtra por especialista.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
    @Operation(summary = "Lista asistentes (opcionalmente filtrados por especialista)")
    public List<AssistantDto> listAssistants(@RequestParam(required = false) UUID specialistId) {
        return assistantAdminService.listAssistants(specialistId);
    }

    /**
     * Obtiene un asistente por userId.
     */
    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
    @Operation(summary = "Obtiene un asistente por el ID de usuario")
    public AssistantDto getAssistantByUserId(@PathVariable UUID userId) {
        return assistantAdminService.getAssistantByUserId(userId);
    }

    /**
     * Reemplaza la lista de especialistas asociados a un asistente.
     */
    @PutMapping("/{id}/specialists")
    @PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
    @Operation(summary = "Actualiza los especialistas asociados a un asistente")
    public AssistantDto updateAssistantSpecialists(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAssistantSpecialistsRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        UUID currentUserId = currentUser != null ? currentUser.getId() : null;
        return assistantAdminService.updateAssistantSpecialists(id, request.getSpecialistIds(), currentUserId);
    }

    /**
     * Lista especialistas asociados a un asistente.
     */
    @GetMapping("/{id}/specialists")
    @PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
    @Operation(summary = "Lista especialistas asociados a un asistente")
    public List<SpecialistDto> listSpecialistsForAssistant(@PathVariable UUID id) {
        return assistantAdminService.listSpecialistsForAssistant(id);
    }
}
