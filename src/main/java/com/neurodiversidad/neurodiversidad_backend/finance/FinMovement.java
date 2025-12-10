package com.neurodiversidad.neurodiversidad_backend.finance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fin_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinMovement {

	@Id
	@GeneratedValue
	@Column(columnDefinition = "uuid")
	private UUID id;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private MovementType type;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "amount", nullable = false, precision = 12, scale = 2)
	private BigDecimal amount;

	@Column(name = "movement_date", nullable = false)
	private LocalDate movementDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", nullable = false)
	private PaymentMethod paymentMethod;

	// Auditoría
	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	@Column(name = "created_by")
	private UUID createdBy;

	@Column(name = "updated_at")
	private OffsetDateTime updatedAt;

	@Column(name = "updated_by")
	private UUID updatedBy;

	@Column(name = "deleted_at")
	private OffsetDateTime deletedAt;
}
