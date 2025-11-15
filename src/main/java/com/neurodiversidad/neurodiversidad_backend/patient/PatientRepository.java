package com.neurodiversidad.neurodiversidad_backend.patient;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {

    List<Patient> findByFullNameContainingIgnoreCaseAndDeletedAtIsNull(String fullName);
}
