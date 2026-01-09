package com.neurodiversidad.neurodiversidad_backend.staff;

import com.neurodiversidad.neurodiversidad_backend.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/staff/specialists")
@Tag(name = "Specialists", description = "Administración de especialistas")
@RequiredArgsConstructor
public class SpecialistAdminController {

    private final SpecialistAdminService specialistAdminService;

    /**
     * Crea y asocia un especialista a un usuario del sistema.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
    @Operation(summary = "Registra un especialista asociado a un usuario del sistema")
    public SpecialistDto createSpecialist(
            @Valid @RequestBody CreateSpecialistRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        UUID currentUserId = currentUser != null ? currentUser.getId() : null;
        return specialistAdminService.createSpecialist(request, currentUserId);
    }

    /**
     * Buscar especialistas por especialidad o nombre.
     * GET /api/v1/staff/specialists?specialty=PSICOLOGIA
     * GET /api/v1/staff/specialists?name=Consuelo
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
    @Operation(summary = "Busca especialistas por especialidad o nombre")
    public List<SpecialistDto> searchSpecialists(
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String name
    ) {
        return specialistAdminService.searchSpecialists(specialty, name);
    }

    /**
     * Obtener especialista por ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
    @Operation(summary = "Obtiene un especialista por su ID")
    public SpecialistDto getSpecialistById(@PathVariable UUID id) {
        return specialistAdminService.getById(id);
    }
}
