package com.neurodiversidad.neurodiversidad_backend.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    private String name;
    private String email;
    private String username;
    private boolean enabled;
    private Set<String> roles;
}
