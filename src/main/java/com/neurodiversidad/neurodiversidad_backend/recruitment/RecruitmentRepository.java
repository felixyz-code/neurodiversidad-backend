package com.neurodiversidad.neurodiversidad_backend.recruitment;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecruitmentRepository extends JpaRepository<Recruitment, UUID> {

	Optional<Recruitment> findByIdAndDeletedAtIsNull(UUID id);

	@Query("""
			SELECT r
			FROM Recruitment r
			WHERE (:status = 'all'
			   OR (:status = 'active' AND r.deletedAt IS NULL)
			   OR (:status = 'deleted' AND r.deletedAt IS NOT NULL))
			  AND (:text IS NULL OR LOWER(CAST(r.nombre AS string)) LIKE LOWER(CAST(CONCAT('%', :text, '%') AS string)))
			  AND (:tipoServicio IS NULL OR r.tipoServicio = :tipoServicio)
			  AND (:estatus IS NULL OR r.estatus = :estatus)
			  AND (r.fechaInicio >= COALESCE(:from, r.fechaInicio))
			  AND (r.fechaInicio <= COALESCE(:to, r.fechaInicio))
			""")
	Page<Recruitment> search(
			@Param("status") String status,
			@Param("text") String text,
			@Param("tipoServicio") RecruitmentServiceType tipoServicio,
			@Param("estatus") RecruitmentStatus estatus,
			@Param("from") LocalDate from,
			@Param("to") LocalDate to,
			Pageable pageable
	);
}
