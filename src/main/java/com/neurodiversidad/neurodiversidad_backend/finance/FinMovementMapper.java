package com.neurodiversidad.neurodiversidad_backend.finance;

import org.springframework.stereotype.Component;

@Component
public class FinMovementMapper {

	public FinMovementDto toDto(FinMovement entity) {
		if (entity == null)
			return null;

		return FinMovementDto.builder().id(entity.getId()).type(entity.getType()).description(entity.getDescription())
				.amount(entity.getAmount()).movementDate(entity.getMovementDate())
				.paymentMethod(entity.getPaymentMethod()).createdAt(entity.getCreatedAt())
				.createdBy(entity.getCreatedBy()).updatedAt(entity.getUpdatedAt()).updatedBy(entity.getUpdatedBy())
				.build();
	}
}
