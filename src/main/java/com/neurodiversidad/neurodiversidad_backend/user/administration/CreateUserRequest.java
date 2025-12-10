package com.neurodiversidad.neurodiversidad_backend.user.administration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class CreateUserRequest {

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    @NotBlank
    @Size(max = 100)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // 👈 no se devuelve nunca en JSON
    private String password;
    
 // Opcional pero recomendado
    @Size(min = 8, message = "La confirmación debe tener al menos 8 caracteres")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String confirmPassword;

    /**
     * Lista de nombres de roles, ej:
     * ["ROLE_DIRECTOR_GENERAL","ROLE_ESPECIALISTA"]
     */
    private List<String> roles;

    /**
     * Por defecto lo pondremos en true si viene null.
     */
    private Boolean enabled;
}
