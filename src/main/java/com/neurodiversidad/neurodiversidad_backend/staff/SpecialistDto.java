package com.neurodiversidad.neurodiversidad_backend.staff;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SpecialistDto {

    private UUID id;           // id del specialist
    private UUID userId;       // id del usuario asociado
    private String userName;   // nombre del usuario
    private String specialty;  // PSICOLOGIA, PEDAGOGIA, etc.
}
