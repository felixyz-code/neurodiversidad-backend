package com.neurodiversidad.neurodiversidad_backend.staff;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpecialistRepository extends JpaRepository<Specialist, UUID> {

	Optional<Specialist> findByUserId(UUID userId);
}
