package com.neurodiversidad.neurodiversidad_backend.patient;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreatePatientRequest {

    @NotBlank(message = "El nombre completo es obligatorio")
    private String fullName;

    @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
    private LocalDate birthDate;

    @Size(max = 50, message = "El teléfono no debe exceder 50 caracteres")
    private String phone;

    @Email(message = "El correo electrónico no es válido")
    @Size(max = 255, message = "El correo electrónico no debe exceder 255 caracteres")
    private String email;

    @Size(max = 2000, message = "Las notas no deben exceder 2000 caracteres")
    private String notes;
}
