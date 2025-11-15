package com.neurodiversidad.neurodiversidad_backend.user;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
	private UUID id;
	private String name;
	private String email;
	private String username;
	private boolean enabled;
	private Set<String> roles;
}
