package com.neurodiversidad.neurodiversidad_backend.finance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FinMovementRepository extends JpaRepository<FinMovement, UUID> {

	Optional<FinMovement> findByIdAndDeletedAtIsNull(UUID id);

	@Query("""
			SELECT m
			FROM FinMovement m
			WHERE m.deletedAt IS NULL
			  AND (:type IS NULL OR m.type = :type)
			  AND (:paymentMethod IS NULL OR m.paymentMethod = :paymentMethod)
			  AND m.movementDate BETWEEN :from AND :to
			ORDER BY m.movementDate DESC, m.createdAt DESC
			""")
	List<FinMovement> search(LocalDate from, LocalDate to, MovementType type, PaymentMethod paymentMethod);
}
