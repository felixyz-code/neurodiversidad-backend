package com.neurodiversidad.neurodiversidad_backend.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neurodiversidad.neurodiversidad_backend.util.SortUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
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
	public Page<FinMovementDto> searchMovements(LocalDate from, LocalDate to, String status, MovementType type,
			PaymentMethod paymentMethod, String text, BigDecimal minAmount, BigDecimal maxAmount, List<String> sort,
			int page, int size) {

		String normalizedStatus = normalizeStatus(status);
		String normalizedText = (text == null || text.isBlank()) ? null : text;

		Sort defaultSort = Sort.by(
				Sort.Order.desc("movementDate"),
				Sort.Order.desc("createdAt")
		);
		Sort sortSpec = SortUtils.parseSort(
				sort,
				Set.of("movementDate", "amount", "createdAt", "type", "paymentMethod", "description"),
				defaultSort
		);

		Page<FinMovement> result = finMovementRepository.search(
				from,
				to,
				normalizedStatus,
				type,
				paymentMethod,
				normalizedText,
				minAmount,
				maxAmount,
				PageRequest.of(page, size, sortSpec)
		);

		return result.map(finMovementMapper::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	public FinSummaryDto getSummary(LocalDate from, LocalDate to, String status, MovementType type,
			PaymentMethod paymentMethod, String text, BigDecimal minAmount, BigDecimal maxAmount) {

		String normalizedStatus = normalizeStatus(status);
		String normalizedText = (text == null || text.isBlank()) ? null : text;

		Object summary = finMovementRepository.summarize(
				from,
				to,
				normalizedStatus,
				type,
				paymentMethod,
				normalizedText,
				minAmount,
				maxAmount
		);

		Object[] row = null;
		if (summary instanceof Object[] summaryRow) {
			row = summaryRow;
			if (row.length == 1 && row[0] instanceof Object[] nested) {
				row = nested;
			}
		}

		BigDecimal income = BigDecimal.ZERO;
		BigDecimal outcome = BigDecimal.ZERO;
		if (row != null) {
			if (row.length > 0 && row[0] instanceof Number n0) {
				income = (n0 instanceof BigDecimal bd) ? bd : BigDecimal.valueOf(n0.doubleValue());
			}
			if (row.length > 1 && row[1] instanceof Number n1) {
				outcome = (n1 instanceof BigDecimal bd) ? bd : BigDecimal.valueOf(n1.doubleValue());
			}
		}
		BigDecimal balance = income.subtract(outcome);

		return FinSummaryDto.builder()
				.totalIncome(income)
				.totalOutcome(outcome)
				.balance(balance)
				.build();
	}

	private String normalizeStatus(String status) {
		if (status == null || status.isBlank()) {
			return "active";
		}
		String normalized = status.trim().toLowerCase();
		return switch (normalized) {
			case "active", "deleted", "all" -> normalized;
			default -> throw new IllegalArgumentException("status inválido: " + status);
		};
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
