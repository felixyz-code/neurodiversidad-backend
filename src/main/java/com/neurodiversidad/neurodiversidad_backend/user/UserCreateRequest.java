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
public class UserCreateRequest {

    private String name;
    private String email;
    private String username;
    private String password;
    private Set<String> roles;
}
