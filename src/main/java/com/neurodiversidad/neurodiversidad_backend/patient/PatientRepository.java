package com.neurodiversidad.neurodiversidad_backend.patient;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {

    Page<Patient> findByDeletedAtIsNull(Pageable pageable);

    Page<Patient> findByFullNameContainingIgnoreCaseAndDeletedAtIsNull(String fullName, Pageable pageable);

    Optional<Patient> findByIdAndDeletedAtIsNull(UUID id);
}
