package com.neurodiversidad.neurodiversidad_backend.appointment;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateAppointmentRequest {

	@FutureOrPresent(message = "La cita no puede iniciar en el pasado")
	private OffsetDateTime startAt;

	@Min(15)
	@Max(240)
	private Integer durationMinutes;

	@Size(max = 2000, message = "Las notas no deben exceder 2000 caracteres")
	private String notes;

	private String status;
}
