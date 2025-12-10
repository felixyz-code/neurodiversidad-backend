package com.neurodiversidad.neurodiversidad_backend.security;

import com.neurodiversidad.neurodiversidad_backend.user.User;
import com.neurodiversidad.neurodiversidad_backend.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsernameIgnoreCaseAndDeletedAtIsNull(username)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
		return new CustomUserDetails(user);
	}
}
