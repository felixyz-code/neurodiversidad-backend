package com.neurodiversidad.neurodiversidad_backend.finance;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateFinMovementRequest {

	@NotNull
    private MovementType type;

    @NotBlank
    @Size(max = 500)
    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
    private BigDecimal amount;

    @NotNull
    private LocalDate movementDate;

    @NotNull
    private PaymentMethod paymentMethod;
}
