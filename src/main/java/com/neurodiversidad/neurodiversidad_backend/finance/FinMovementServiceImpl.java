package com.neurodiversidad.neurodiversidad_backend.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FinMovementServiceImpl implements FinMovementService {

	private final FinMovementRepository finMovementRepository;
	private final FinMovementMapper finMovementMapper;

	@Override
	public FinMovementDto createMovement(CreateFinMovementRequest request, UUID currentUserId) {

		MovementType type = request.getType();
		PaymentMethod paymentMethod = request.getPaymentMethod();

		FinMovement entity = FinMovement.builder().type(type).description(request.getDescription())
				.amount(request.getAmount()).movementDate(request.getMovementDate()).paymentMethod(paymentMethod)
				.createdAt(OffsetDateTime.now()).createdBy(currentUserId).build();

		entity = finMovementRepository.save(entity);

		return finMovementMapper.toDto(entity);
	}

	@Override
	public FinMovementDto updateMovement(UUID id, UpdateFinMovementRequest request, UUID currentUserId) {

		FinMovement entity = finMovementRepository.findByIdAndDeletedAtIsNull(id)
				.orElseThrow(() -> new IllegalArgumentException("Movimiento no encontrado con id: " + id));

		if (request.getType() != null) {
			entity.setType(request.getType());
		}

		if (request.getDescription() != null) {
			entity.setDescription(request.getDescription());
		}

		if (request.getAmount() != null) {
			entity.setAmount(request.getAmount());
		}

		if (request.getMovementDate() != null) {
			entity.setMovementDate(request.getMovementDate());
		}

		if (request.getPaymentMethod() != null) {
			entity.setPaymentMethod(request.getPaymentMethod());
		}

		entity.setUpdatedAt(OffsetDateTime.now());
		entity.setUpdatedBy(currentUserId);

		entity = finMovementRepository.save(entity);

		return finMovementMapper.toDto(entity);
	}

	@Override
	public void deleteMovement(UUID id, UUID currentUserId) {
		FinMovement entity = finMovementRepository.findByIdAndDeletedAtIsNull(id)
				.orElseThrow(() -> new IllegalArgumentException("Movimiento no encontrado con id: " + id));

		entity.setDeletedAt(OffsetDateTime.now());
		entity.setUpdatedAt(OffsetDateTime.now());
		entity.setUpdatedBy(currentUserId);

		finMovementRepository.save(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public FinMovementDto getMovementById(UUID id) {
		FinMovement entity = finMovementRepository.findByIdAndDeletedAtIsNull(id).stream().findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Movimiento no encontrado con id: " + id));

		return finMovementMapper.toDto(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public List<FinMovementDto> searchMovements(LocalDate from, LocalDate to, MovementType type,
			PaymentMethod paymentMethod) {

		List<FinMovement> result = finMovementRepository.search(from, to, type, paymentMethod);
		return result.stream().map(finMovementMapper::toDto).toList();
	}

	@Override
	public void restoreMovement(UUID id, UUID currentUserId) {
		
		FinMovement movement = finMovementRepository.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Movimiento no encontrado con id: " + id));

	    // Si ya NO está borrado, puedes decidir si lanzar excepción o simplemente ignorar
	    if (movement.getDeletedAt() == null) {
	    	throw new IllegalStateException("El movimiento no está borrado");
	    }

	    movement.setDeletedAt(null);
	    movement.setUpdatedAt(OffsetDateTime.now());
	    movement.setUpdatedBy(currentUserId);

	    finMovementRepository.save(movement);
		
	}
}
