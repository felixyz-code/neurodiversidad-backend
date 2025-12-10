package com.neurodiversidad.neurodiversidad_backend.patient;

import org.springframework.data.domain.Page;

import java.util.UUID;

public interface PatientService {

	Page<PatientDto> searchPatients(String search, int page, int size);

	PatientDto getPatientById(UUID id);

	PatientDto createPatient(CreatePatientRequest request, UUID createdBy);

	PatientDto updatePatient(UUID id, UpdatePatientRequest request, UUID updatedBy);

	void deletePatient(UUID id, UUID updatedBy);
}
