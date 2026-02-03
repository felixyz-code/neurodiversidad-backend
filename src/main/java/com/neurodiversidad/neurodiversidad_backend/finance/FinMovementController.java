package com.neurodiversidad.neurodiversidad_backend.finance;

import com.neurodiversidad.neurodiversidad_backend.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/finances/movements")
@RequiredArgsConstructor
public class FinMovementController {

	private final FinMovementService finMovementService;

	/**
	 * Crear movimiento financiero (ingreso / egreso) POST
	 * /api/v1/finances/movements
	 */
	@PostMapping
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'FINANZAS', 'ASISTENTE_GENERAL')")
	public ResponseEntity<FinMovementDto> createMovement(@Valid @RequestBody CreateFinMovementRequest request,
			@AuthenticationPrincipal CustomUserDetails currentUser) {

		UUID currentUserId = currentUser != null ? currentUser.getId() : null;

		FinMovementDto created = finMovementService.createMovement(request, currentUserId);

		return ResponseEntity.ok(created);
	}

	/**
	 * Obtener movimiento por ID GET /api/v1/finances/movements/{id}
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'FINANZAS', 'ASISTENTE_GENERAL')")
	public ResponseEntity<FinMovementDto> getMovementById(@PathVariable UUID id) {
		FinMovementDto dto = finMovementService.getMovementById(id);
		return ResponseEntity.ok(dto);
	}

	/**
	 * Buscar movimientos por rango de fechas y filtros opcionales. GET
	 * /api/v1/finances/movements?from=YYYY-MM-DD&to=YYYY-MM-DD[&status=active|deleted|all][&type=INCOME|OUTCOME][&paymentMethod=CASH|CARD|TRANSFER|OTHER]
	 */
	@GetMapping
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'FINANZAS', 'ASISTENTE_GENERAL')")
	public ResponseEntity<org.springframework.data.domain.Page<FinMovementDto>> searchMovements(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
			@RequestParam(required = false, defaultValue = "active") String status,
			@RequestParam(required = false) MovementType type,
			@RequestParam(required = false) PaymentMethod paymentMethod,
			@RequestParam(required = false) String text,
			@RequestParam(required = false) BigDecimal minAmount,
			@RequestParam(required = false) BigDecimal maxAmount,
			@RequestParam(required = false) java.util.List<String> sort,
			@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "20") int size) {

		var list = finMovementService.searchMovements(
				from,
				to,
				status,
				type,
				paymentMethod,
				text,
				minAmount,
				maxAmount,
				sort,
				page,
				size
		);
		return ResponseEntity.ok(list);
	}

	/**
	 * Resumen/KPIs de movimientos financieros.
	 * GET /api/v1/finances/movements/summary?from=YYYY-MM-DD&to=YYYY-MM-DD[&status=active|deleted|all][&type=INCOME|OUTCOME][&paymentMethod=CASH|CARD|TRANSFER|OTHER]
	 */
	@GetMapping("/summary")
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'FINANZAS', 'ASISTENTE_GENERAL')")
	public ResponseEntity<FinSummaryDto> getSummary(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
			@RequestParam(required = false, defaultValue = "active") String status,
			@RequestParam(required = false) MovementType type,
			@RequestParam(required = false) PaymentMethod paymentMethod,
			@RequestParam(required = false) String text,
			@RequestParam(required = false) BigDecimal minAmount,
			@RequestParam(required = false) BigDecimal maxAmount) {

		FinSummaryDto summary = finMovementService.getSummary(from, to, status, type, paymentMethod, text, minAmount, maxAmount);
		return ResponseEntity.ok(summary);
	}

	/**
	 * Actualizar movimiento PUT /api/v1/finances/movements/{id}
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'FINANZAS', 'ASISTENTE_GENERAL')")
	public ResponseEntity<FinMovementDto> updateMovement(@PathVariable UUID id,
			@Valid @RequestBody UpdateFinMovementRequest request,
			@AuthenticationPrincipal CustomUserDetails currentUser) {

		UUID currentUserId = currentUser != null ? currentUser.getId() : null;

		FinMovementDto updated = finMovementService.updateMovement(id, request, currentUserId);
		return ResponseEntity.ok(updated);
	}

	/**
	 * Borrado lógico del movimiento DELETE /api/v1/finances/movements/{id}
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'FINANZAS', 'ASISTENTE_GENERAL')")
	public ResponseEntity<Void> deleteMovement(@PathVariable UUID id,
			@AuthenticationPrincipal CustomUserDetails currentUser) {

		UUID currentUserId = currentUser != null ? currentUser.getId() : null;

		finMovementService.deleteMovement(id, currentUserId);
		return ResponseEntity.noContent().build();
	}
	
	/**
	 * Restaurar un movimiento borrado lógicamente.
	 * PATCH /api/v1/finances/movements/{id}/restore
	 */
	@PatchMapping("/{id}/restore")
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'FINANZAS', 'ASISTENTE_GENERAL')")
	public ResponseEntity<Void> restoreMovement(
	        @PathVariable UUID id,
	        @AuthenticationPrincipal CustomUserDetails currentUser) {

	    UUID currentUserId = currentUser != null ? currentUser.getId() : null;
	    finMovementService.restoreMovement(id, currentUserId);

	    return ResponseEntity.noContent().build();
	}
}
