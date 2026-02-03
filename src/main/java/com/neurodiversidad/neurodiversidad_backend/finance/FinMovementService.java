package com.neurodiversidad.neurodiversidad_backend.finance;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface FinMovementService {

	FinMovementDto createMovement(CreateFinMovementRequest request, UUID currentUserId);

	FinMovementDto updateMovement(UUID id, UpdateFinMovementRequest request, UUID currentUserId);

	void deleteMovement(UUID id, UUID currentUserId);

	FinMovementDto getMovementById(UUID id);

    Page<FinMovementDto> searchMovements(
            LocalDate from,
            LocalDate to,
            String status,
            MovementType type,
            PaymentMethod paymentMethod,
            String text,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            java.util.List<String> sort,
            int page,
            int size
    );

    FinSummaryDto getSummary(
            LocalDate from,
            LocalDate to,
            String status,
            MovementType type,
            PaymentMethod paymentMethod,
            String text,
            BigDecimal minAmount,
            BigDecimal maxAmount
    );
	
	void restoreMovement(UUID id, UUID currentUserId);
}
