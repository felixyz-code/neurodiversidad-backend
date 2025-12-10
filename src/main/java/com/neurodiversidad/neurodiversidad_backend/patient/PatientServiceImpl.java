package com.neurodiversidad.neurodiversidad_backend.patient;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImpl implements PatientService {

	private final PatientRepository patientRepository;
	private final PatientMapper patientMapper;

	@Override
	@Transactional(readOnly = true)
	public Page<PatientDto> searchPatients(String search, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);

		Page<Patient> result;
		if (search == null || search.isBlank()) {
			result = patientRepository.findByDeletedAtIsNull(pageable);
		} else {
			result = patientRepository.findByFullNameContainingIgnoreCaseAndDeletedAtIsNull(search, pageable);
		}

		return result.map(patientMapper::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	public PatientDto getPatientById(UUID id) {
		Patient patient = patientRepository.findByIdAndDeletedAtIsNull(id)
				.orElseThrow(() -> new PatientNotFoundException("Paciente no encontrado con id: " + id));

		return patientMapper.toDto(patient);
	}

	@Override
	public PatientDto createPatient(CreatePatientRequest request, UUID createdBy) {
		Patient patient = Patient.builder().fullName(request.getFullName()).birthDate(request.getBirthDate())
				.phone(request.getPhone()).email(request.getEmail()).notes(request.getNotes())
				.createdAt(OffsetDateTime.now()).createdBy(createdBy).build();

		patient = patientRepository.save(patient);
		return patientMapper.toDto(patient);
	}

	@Override
	public PatientDto updatePatient(UUID id, UpdatePatientRequest request, UUID updatedUser) {
		Patient patient = patientRepository.findByIdAndDeletedAtIsNull(id)
				.orElseThrow(() -> new PatientNotFoundException("Paciente no encontrado con id: " + id));

		patient.setFullName(request.getFullName());
		patient.setBirthDate(request.getBirthDate());
		patient.setPhone(request.getPhone());
		patient.setEmail(request.getEmail());
		patient.setNotes(request.getNotes());
		patient.setUpdatedAt(OffsetDateTime.now());
		patient.setUpdatedBy(updatedUser);

		patient = patientRepository.save(patient);
		return patientMapper.toDto(patient);
	}

	@Override
	public void deletePatient(UUID id, UUID updatedBy) {
		Patient patient = patientRepository.findByIdAndDeletedAtIsNull(id)
				.orElseThrow(() -> new PatientNotFoundException("Paciente no encontrado con id: " + id));

		patient.setDeletedAt(OffsetDateTime.now());
		patient.setUpdatedBy(updatedBy); 
		patientRepository.save(patient); // Soft delete
	}
}
