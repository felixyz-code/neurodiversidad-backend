package com.neurodiversidad.neurodiversidad_backend.recruitment;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neurodiversidad.neurodiversidad_backend.security.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/recruitment")
@RequiredArgsConstructor
public class RecruitmentController {

	private final RecruitmentService recruitmentService;

	@GetMapping
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
	public ResponseEntity<org.springframework.data.domain.Page<RecruitmentDto>> search(
			@RequestParam(required = false) String text,
			@RequestParam(required = false) RecruitmentServiceType tipoServicio,
			@RequestParam(required = false) RecruitmentStatus estatus,
			@RequestParam(required = false, defaultValue = "active") String status,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
			@RequestParam(required = false) java.util.List<String> sort,
			@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "20") int size) {

		var result = recruitmentService.search(status, text, tipoServicio, estatus, from, to, sort, page, size);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
	public ResponseEntity<RecruitmentDto> getById(@PathVariable UUID id) {
		RecruitmentDto dto = recruitmentService.getById(id);
		return ResponseEntity.ok(dto);
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
	public ResponseEntity<RecruitmentDto> create(@Valid @RequestBody CreateRecruitmentRequest request,
			@AuthenticationPrincipal CustomUserDetails currentUser) {

		UUID currentUserId = currentUser != null ? currentUser.getId() : null;
		RecruitmentDto created = recruitmentService.create(request, currentUserId);
		return ResponseEntity.ok(created);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
	public ResponseEntity<RecruitmentDto> update(@PathVariable UUID id,
			@Valid @RequestBody UpdateRecruitmentRequest request,
			@AuthenticationPrincipal CustomUserDetails currentUser) {

		UUID currentUserId = currentUser != null ? currentUser.getId() : null;
		RecruitmentDto updated = recruitmentService.update(id, request, currentUserId);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
	public ResponseEntity<Void> delete(@PathVariable UUID id,
			@AuthenticationPrincipal CustomUserDetails currentUser) {

		UUID currentUserId = currentUser != null ? currentUser.getId() : null;
		recruitmentService.delete(id, currentUserId);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{id}/restore")
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
	public ResponseEntity<Void> restore(@PathVariable UUID id,
			@AuthenticationPrincipal CustomUserDetails currentUser) {

		UUID currentUserId = currentUser != null ? currentUser.getId() : null;
		recruitmentService.restore(id, currentUserId);
		return ResponseEntity.noContent().build();
	}
}
