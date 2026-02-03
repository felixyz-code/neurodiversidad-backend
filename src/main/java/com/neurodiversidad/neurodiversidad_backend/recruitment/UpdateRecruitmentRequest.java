package com.neurodiversidad.neurodiversidad_backend.recruitment;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateRecruitmentRequest {

	@Size(max = 200)
	private String nombre;

	private RecruitmentServiceType tipoServicio;

	private LocalDate fechaInicio;

	private LocalDate fechaSalida;

	private RecruitmentStatus estatus;
}
