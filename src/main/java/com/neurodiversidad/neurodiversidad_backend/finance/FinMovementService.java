package com.neurodiversidad.neurodiversidad_backend.finance;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FinMovementService {

	FinMovementDto createMovement(CreateFinMovementRequest request, UUID currentUserId);

	FinMovementDto updateMovement(UUID id, UpdateFinMovementRequest request, UUID currentUserId);

	void deleteMovement(UUID id, UUID currentUserId);

	FinMovementDto getMovementById(UUID id);

	List<FinMovementDto> searchMovements(LocalDate from, LocalDate to, MovementType type, PaymentMethod paymentMethod);
	
	void restoreMovement(UUID id, UUID currentUserId);
}
