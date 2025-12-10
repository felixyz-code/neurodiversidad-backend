package com.neurodiversidad.neurodiversidad_backend.finance;

import com.neurodiversidad.neurodiversidad_backend.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
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
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'FINANZAS')")
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
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'FINANZAS')")
	public ResponseEntity<FinMovementDto> getMovementById(@PathVariable UUID id) {
		FinMovementDto dto = finMovementService.getMovementById(id);
		return ResponseEntity.ok(dto);
	}

	/**
	 * Buscar movimientos por rango de fechas y filtros opcionales. GET
	 * /api/v1/finances/movements?from=YYYY-MM-DD&to=YYYY-MM-DD[&type=INCOME|OUTCOME][&paymentMethod=CASH|CARD|TRANSFER|OTHER]
	 */
	@GetMapping
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'FINANZAS')")
	public ResponseEntity<List<FinMovementDto>> searchMovements(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
			@RequestParam(required = false) MovementType type,
			@RequestParam(required = false) PaymentMethod paymentMethod) {

		List<FinMovementDto> list = finMovementService.searchMovements(from, to, type, paymentMethod);
		return ResponseEntity.ok(list);
	}

	/**
	 * Actualizar movimiento PUT /api/v1/finances/movements/{id}
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'FINANZAS')")
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
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'FINANZAS')")
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
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'FINANZAS')")
	public ResponseEntity<Void> restoreMovement(
	        @PathVariable UUID id,
	        @AuthenticationPrincipal CustomUserDetails currentUser) {

	    UUID currentUserId = currentUser != null ? currentUser.getId() : null;
	    finMovementService.restoreMovement(id, currentUserId);

	    return ResponseEntity.noContent().build();
	}
}
