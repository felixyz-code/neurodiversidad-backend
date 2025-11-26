package com.neurodiversidad.neurodiversidad_backend.appointment;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class CreateAppointmentRequest {

    @NotNull(message = "El especialista es obligatorio")
    private UUID specialistId;

    @NotNull(message = "El paciente es obligatorio")
    private UUID patientId;

    @NotNull(message = "La fecha/hora de inicio es obligatoria")
    @FutureOrPresent(message = "La cita no puede iniciar en el pasado")
    private OffsetDateTime startAt;
    
    @NotNull
    @Min(15)
    @Max(240)
    private Integer durationMinutes;

    @Size(max = 2000, message = "Las notas no deben exceder 2000 caracteres")
    private String notes;

    // Opcional, si no lo mandan ponemos PENDING
    private String status;
}
