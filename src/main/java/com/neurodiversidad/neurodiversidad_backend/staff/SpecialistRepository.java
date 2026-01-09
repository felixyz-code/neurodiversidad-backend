package com.neurodiversidad.neurodiversidad_backend.staff;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpecialistRepository extends JpaRepository<Specialist, UUID> {

    Optional<Specialist> findByUserId(UUID userId);

    List<Specialist> findBySpecialty(String specialty);

    List<Specialist> findByUser_NameContainingIgnoreCase(String name);
}
