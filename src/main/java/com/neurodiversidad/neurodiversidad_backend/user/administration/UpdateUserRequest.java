package com.neurodiversidad.neurodiversidad_backend.user.administration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UpdateUserRequest {

    @Size(max = 255)
    private String name;

    @Email
    @Size(max = 255)
    private String email;

    @Size(max = 100)
    private String username;

    /**
     * Si se envía, se resetea password.
     */
    @Size(min = 6, max = 100)
    private String newPassword;

    /**
     * Lista de nombres de roles a dejar asignados.
     * Si es null, no se tocan los roles.
     */
    private List<String> roles;

    /**
     * Si es null, no se toca el enabled.
     */
    private Boolean enabled;
}
