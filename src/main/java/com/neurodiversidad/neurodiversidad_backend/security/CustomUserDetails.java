package com.neurodiversidad.neurodiversidad_backend.security;

import com.neurodiversidad.neurodiversidad_backend.user.Role;
import com.neurodiversidad.neurodiversidad_backend.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

	private final UUID id;
	private final String username;
	private final String password;
	private final boolean enabled;
	private final Set<GrantedAuthority> authorities;

	public CustomUserDetails(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.password = user.getPasswordHash();
		this.enabled = user.isEnabled();
		this.authorities = user.getRoles().stream().map(Role::getName) // ROLE_DIRECTOR_GENERAL, etc.
				.map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
	}

	public UUID getId() {
		return id;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}
