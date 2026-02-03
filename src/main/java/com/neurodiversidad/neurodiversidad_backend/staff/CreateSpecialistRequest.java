package com.neurodiversidad.neurodiversidad_backend.staff;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateSpecialistRequest {

    @NotNull(message = "El userId es obligatorio")
    private UUID userId;

    /**
     * Especialidad del especialista:
     * PSICOLOGIA, PEDAGOGIA, FISIOTERAPIA, PEDIATRIA, OTRA
     */
    @NotBlank(message = "La especialidad es obligatoria")
    private String specialty;
}
