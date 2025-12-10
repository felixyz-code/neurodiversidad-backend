package com.neurodiversidad.neurodiversidad_backend.user;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neurodiversidad.neurodiversidad_backend.security.CustomUserDetails;
import com.neurodiversidad.neurodiversidad_backend.security.JwtService;
import com.neurodiversidad.neurodiversidad_backend.security.TokenBlacklistService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Operaciones con usuarios del sistema")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final JwtService jwtService;
	private final TokenBlacklistService tokenBlacklistService;

	@GetMapping
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
	@Operation(summary = "Lista usuarios, con filtro opcional por username")
	public List<UserDTO> getUsers(@RequestParam(required = false) String username) {
		return userService.findUsers(username);
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
	@Operation(summary = "Crea un nuevo usuario")
	public UserDTO createUser(@RequestBody UserCreateRequest request,
			@AuthenticationPrincipal CustomUserDetails currentUser) {
		UUID createdBy = currentUser != null ? currentUser.getId() : null;
		return userService.createUser(request, createdBy);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
	@Operation(summary = "Actualiza un usuario existente")
	public UserDTO updateUser(@PathVariable UUID id, @RequestBody UserUpdateRequest request,
			@AuthenticationPrincipal CustomUserDetails currentUser) {
		UUID modifiedBy = currentUser != null ? currentUser.getId() : null;
		return userService.updateUser(id, request, modifiedBy);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('DIRECTOR_GENERAL', 'ASISTENTE_GENERAL', 'RRHH')")
	@Operation(summary = "Elimina (lógicamente) un usuario")
	public void deleteUser(@PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails currentUser) {
		UUID deletedBy = currentUser != null ? currentUser.getId() : null;
		userService.deleteUser(id, deletedBy);
	}

	@PostMapping("/logout")
	@PreAuthorize("isAuthenticated()")
	@Operation(summary = "Cierra sesión anulando el JWT actual")
	public String logout(@AuthenticationPrincipal CustomUserDetails currentUser,
			@RequestHeader("Authorization") String authHeader) {

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new IllegalArgumentException("No se envió un token válido");
		}

		String token = authHeader.substring(7);

		Instant expiration = jwtService.extractExpiration(token).toInstant();

		tokenBlacklistService.blacklistToken(token, expiration);

		return "Sesión cerrada correctamente";
	}

}
