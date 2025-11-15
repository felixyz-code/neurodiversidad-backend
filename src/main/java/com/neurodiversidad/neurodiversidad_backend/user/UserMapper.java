package com.neurodiversidad.neurodiversidad_backend.user;

import java.util.stream.Collectors;

public class UserMapper {

	public static UserDTO toDTO(User user) {
		return UserDTO.builder().id(user.getId()).name(user.getName()).email(user.getEmail())
				.username(user.getUsername()).enabled(user.isEnabled())
				.roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())).build();
	}
}
