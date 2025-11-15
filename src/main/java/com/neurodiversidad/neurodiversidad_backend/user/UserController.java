package com.neurodiversidad.neurodiversidad_backend.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Operaciones básicas con usuarios")
public class UserController {

	private final UserRepository userRepository;

	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping
	@Operation(summary = "Lista todos los usuarios registrados")
	public List<UserDTO> getUsers() {
		return userRepository.findAll().stream().map(UserMapper::toDTO).toList();
	}
}
