package com.neurodiversidad.neurodiversidad_backend.auth;

import com.neurodiversidad.neurodiversidad_backend.security.JwtService;
import com.neurodiversidad.neurodiversidad_backend.user.User;
import com.neurodiversidad.neurodiversidad_backend.user.UserDTO;
import com.neurodiversidad.neurodiversidad_backend.user.UserMapper;
import com.neurodiversidad.neurodiversidad_backend.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Autenticación y emisión de tokens JWT")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final UserRepository userRepository;

	@Value("${security.jwt.expiration-ms}")
	private long expirationMs;

	public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
			UserRepository userRepository) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.userRepository = userRepository;
	}

	@PostMapping("/login")
	@Operation(summary = "Autentica un usuario y devuelve un JWT")
	public LoginResponse login(@RequestBody LoginRequest request) {

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		UserDetails principal = (UserDetails) authentication.getPrincipal();
		String token = jwtService.generateToken(principal);

		User user = userRepository.findByUsernameIgnoreCaseAndDeletedAtIsNull(principal.getUsername()).orElseThrow();

		UserDTO userDTO = UserMapper.toDTO(user);

		return new LoginResponse(token, expirationMs, userDTO);
	}
}
