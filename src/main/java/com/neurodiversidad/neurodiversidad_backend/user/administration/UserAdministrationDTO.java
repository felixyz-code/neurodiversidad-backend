package com.neurodiversidad.neurodiversidad_backend.user.administration;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAdministrationDTO {

    private UUID id;

    private String name;

    private String email;

    private String username;

    private boolean enabled;

    /**
     * Nombres de roles, ej: ["ROLE_DIRECTOR_GENERAL","ROLE_ESPECIALISTA"]
     */
    private List<String> roles;

    private OffsetDateTime lastLoginAt;

    private OffsetDateTime createdAt;

    private UUID createdBy;

    private OffsetDateTime updatedAt;

    private UUID updatedBy;

    private OffsetDateTime deletedAt;

    private UUID deletedBy;
}