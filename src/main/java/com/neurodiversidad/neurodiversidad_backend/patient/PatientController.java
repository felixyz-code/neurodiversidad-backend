package com.neurodiversidad.neurodiversidad_backend.patient;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.neurodiversidad.neurodiversidad_backend.security.CustomUserDetails;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@Validated
public class PatientController {

	private final PatientService patientService;

	/**
	 * Buscar / listar pacientes con paginación. Ejemplo: GET
	 * /api/v1/patients?search=juan&page=0&size=10
	 */
	@GetMapping
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'ASISTENTE', 'TERAPEUTA')")
	public Page<PatientDto> searchPatients(@RequestParam(required = false, defaultValue = "") String search,
			@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "20") int size) {
		return patientService.searchPatients(search, page, size);
	}

	/**
	 * Obtener detalle de un paciente por id. GET /api/v1/patients/{id}
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'ASISTENTE', 'TERAPEUTA')")
	public PatientDto getPatientById(@PathVariable UUID id) {
		return patientService.getPatientById(id);
	}

	/**
	 * Crear un nuevo paciente. POST /api/v1/patients
	 */
	@PostMapping
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'ASISTENTE', 'TERAPEUTA')")
	public ResponseEntity<PatientDto> createPatient(@Valid @RequestBody CreatePatientRequest request,
			@AuthenticationPrincipal CustomUserDetails currentUser) {

		UUID createdBy = currentUser != null ? currentUser.getId() : null;
		PatientDto created = patientService.createPatient(request, createdBy);

		// Opcional: construir Location header con la URL del recurso creado
		URI location = URI.create(String.format("/api/v1/patients/%s", created.getId()));

		return ResponseEntity.created(location).body(created);
	}

	/**
	 * Actualizar un paciente. PUT /api/v1/patients/{id}
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'ASISTENTE', 'TERAPEUTA')")
	public PatientDto updatePatient(@PathVariable UUID id, @Valid @RequestBody UpdatePatientRequest request,
			@AuthenticationPrincipal CustomUserDetails currentUser) {
		UUID updatedBy = currentUser != null ? currentUser.getId() : null;

		return patientService.updatePatient(id, request, updatedBy);
	}

	/**
	 * Borrado lógico (soft delete) de un paciente. DELETE /api/v1/patients/{id}
	 * Solo Director General y Asistente General.
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL')")
	public ResponseEntity<Void> deletePatient(@PathVariable UUID id,
			@AuthenticationPrincipal CustomUserDetails currentUser) {
		UUID updatedBy = currentUser != null ? currentUser.getId() : null;
		
		patientService.deletePatient(id, updatedBy);
		return ResponseEntity.noContent().build();
	}
}
