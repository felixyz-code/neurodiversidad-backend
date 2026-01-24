package com.neurodiversidad.neurodiversidad_backend.user.administration;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserIdNameDTO {

    private UUID id;
    private String name;
    private String username;
}
