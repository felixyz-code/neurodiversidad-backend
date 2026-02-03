package com.neurodiversidad.neurodiversidad_backend.user.administration;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserIdsRequest {

    @NotEmpty(message = "Se requiere al menos un userId")
    private List<UUID> userIds;
}
