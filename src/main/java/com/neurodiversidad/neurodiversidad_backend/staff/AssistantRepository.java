package com.neurodiversidad.neurodiversidad_backend.staff;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssistantRepository extends JpaRepository<Assistant, UUID> {

	Optional<Assistant> findByUserId(UUID userId);

	List<Assistant> findBySpecialistId(UUID specialistId);
}
