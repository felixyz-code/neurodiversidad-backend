package com.neurodiversidad.neurodiversidad_backend.recruitment;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRecruitmentRequest {

	@NotBlank
	@Size(max = 200)
	private String nombre;

	@NotNull
	private RecruitmentServiceType tipoServicio;

	@NotNull
	private LocalDate fechaInicio;

	private LocalDate fechaSalida;

	@NotNull
	private RecruitmentStatus estatus;
}
