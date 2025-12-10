package com.neurodiversidad.neurodiversidad_backend.finance;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateFinMovementRequest {

	private MovementType type;

	@Size(max = 500)
	private String description;

	@DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
	private BigDecimal amount;

	private LocalDate movementDate;

	private PaymentMethod paymentMethod;
}