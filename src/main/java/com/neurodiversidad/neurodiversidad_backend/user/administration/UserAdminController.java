package com.neurodiversidad.neurodiversidad_backend.user.administration;

import com.neurodiversidad.neurodiversidad_backend.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

	private final UserAdminService userAdminService;

	/**
	 * Buscar usuarios con filtros opcionales. GET
	 * /api/v1/admin/users?text=...&enabled=true|false&roleName=ROLE_ESPECIALISTA
	 */
	@GetMapping
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
	public ResponseEntity<List<UserAdministrationDTO>> searchUsers(@RequestParam(required = false) String text,
			@RequestParam(required = false) Boolean enabled, @RequestParam(required = false) String roleName) {
		List<UserAdministrationDTO> list = userAdminService.searchUsers(text, enabled, roleName);
		return ResponseEntity.ok(list);
	}

	/**
	 * Obtener usuario por id GET /api/v1/admin/users/{id}
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
	public ResponseEntity<UserAdministrationDTO> getUser(@PathVariable UUID id) {
		UserAdministrationDTO dto = userAdminService.getUserById(id);
		return ResponseEntity.ok(dto);
	}

	/**
	 * Crear usuario POST /api/v1/admin/users
	 */
	@PostMapping
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
	public ResponseEntity<UserAdministrationDTO> createUser(@Valid @RequestBody CreateUserRequest request,
			@AuthenticationPrincipal CustomUserDetails currentUser) {
		UUID currentUserId = currentUser != null ? currentUser.getId() : null;
		UserAdministrationDTO created = userAdminService.createUser(request, currentUserId);
		URI location = URI.create(String.format("/api/v1/admin/users/%s", created.getId()));
		return ResponseEntity.created(location).body(created);
	}

	/**
	 * Actualizar usuario PUT /api/v1/admin/users/{id}
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
	public ResponseEntity<UserAdministrationDTO> updateUser(@PathVariable UUID id,
			@Valid @RequestBody UpdateUserRequest request, @AuthenticationPrincipal CustomUserDetails currentUser) {
		UUID currentUserId = currentUser != null ? currentUser.getId() : null;
		UserAdministrationDTO updated = userAdminService.updateUser(id, request, currentUserId);
		return ResponseEntity.ok(updated);
	}

	/**
	 * Borrado lógico DELETE /api/v1/admin/users/{id}
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
	public ResponseEntity<Void> deleteUser(@PathVariable UUID id,
			@AuthenticationPrincipal CustomUserDetails currentUser) {
		UUID currentUserId = currentUser != null ? currentUser.getId() : null;
		userAdminService.deleteUser(id, currentUserId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Restaurar usuario (quitar borrado lógico) PATCH
	 * /api/v1/admin/users/{id}/restore
	 */
	@PatchMapping("/{id}/restore")
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
	public ResponseEntity<Void> restoreUser(@PathVariable UUID id,
			@AuthenticationPrincipal CustomUserDetails currentUser) {
		UUID currentUserId = currentUser != null ? currentUser.getId() : null;
		userAdminService.restoreUser(id, currentUserId);
		return ResponseEntity.noContent().build();
	}
}
