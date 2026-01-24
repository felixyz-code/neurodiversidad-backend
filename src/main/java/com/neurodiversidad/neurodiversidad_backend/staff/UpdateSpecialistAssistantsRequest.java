package com.neurodiversidad.neurodiversidad_backend.staff;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UpdateSpecialistAssistantsRequest {

    @NotEmpty(message = "Se requiere al menos un asistente")
    private List<UUID> assistantIds;
}
