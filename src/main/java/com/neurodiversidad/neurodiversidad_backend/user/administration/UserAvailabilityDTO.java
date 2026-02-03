package com.neurodiversidad.neurodiversidad_backend.user.administration;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserAvailabilityDTO {
    private boolean usernameAvailable;
    private boolean emailAvailable;
}
