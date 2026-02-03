package com.neurodiversidad.neurodiversidad_backend.finance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FinMovementRepository extends JpaRepository<FinMovement, UUID> {

	Optional<FinMovement> findByIdAndDeletedAtIsNull(UUID id);

	@Query("""
			SELECT m
			FROM FinMovement m
			WHERE (:status = 'all'
			   OR (:status = 'active' AND m.deletedAt IS NULL)
			   OR (:status = 'deleted' AND m.deletedAt IS NOT NULL))
			  AND (:type IS NULL OR m.type = :type)
			  AND (:paymentMethod IS NULL OR m.paymentMethod = :paymentMethod)
			  AND (:text IS NULL OR LOWER(CAST(m.description AS string)) LIKE LOWER(CAST(CONCAT('%', :text, '%') AS string)))
			  AND (:minAmount IS NULL OR m.amount >= :minAmount)
			  AND (:maxAmount IS NULL OR m.amount <= :maxAmount)
			  AND m.movementDate BETWEEN :from AND :to
			""")
	Page<FinMovement> search(
			@Param("from") LocalDate from,
			@Param("to") LocalDate to,
			@Param("status") String status,
			@Param("type") MovementType type,
			@Param("paymentMethod") PaymentMethod paymentMethod,
			@Param("text") String text,
			@Param("minAmount") BigDecimal minAmount,
			@Param("maxAmount") BigDecimal maxAmount,
			Pageable pageable
	);

	@Query("""
			SELECT
			  COALESCE(SUM(CASE WHEN m.type = 'INCOME' THEN m.amount ELSE 0 END), 0),
			  COALESCE(SUM(CASE WHEN m.type = 'OUTCOME' THEN m.amount ELSE 0 END), 0)
			FROM FinMovement m
			WHERE (:status = 'all'
			   OR (:status = 'active' AND m.deletedAt IS NULL)
			   OR (:status = 'deleted' AND m.deletedAt IS NOT NULL))
			  AND (:type IS NULL OR m.type = :type)
			  AND (:paymentMethod IS NULL OR m.paymentMethod = :paymentMethod)
			  AND (:text IS NULL OR LOWER(CAST(m.description AS string)) LIKE LOWER(CAST(CONCAT('%', :text, '%') AS string)))
			  AND (:minAmount IS NULL OR m.amount >= :minAmount)
			  AND (:maxAmount IS NULL OR m.amount <= :maxAmount)
			  AND m.movementDate BETWEEN :from AND :to
			""")
	Object[] summarize(
			@Param("from") LocalDate from,
			@Param("to") LocalDate to,
			@Param("status") String status,
			@Param("type") MovementType type,
			@Param("paymentMethod") PaymentMethod paymentMethod,
			@Param("text") String text,
			@Param("minAmount") BigDecimal minAmount,
			@Param("maxAmount") BigDecimal maxAmount
	);
}
