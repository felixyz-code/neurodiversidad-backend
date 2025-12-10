package com.neurodiversidad.neurodiversidad_backend.finance;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class FinMovementDto {

    private UUID id;

    private MovementType type;

    private String description;

    private BigDecimal amount;

    private LocalDate movementDate;

    private PaymentMethod paymentMethod;

    private OffsetDateTime createdAt;

    private UUID createdBy;

    private OffsetDateTime updatedAt;

    private UUID updatedBy;
}