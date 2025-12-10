package com.neurodiversidad.neurodiversidad_backend.user.administration;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.neurodiversidad.neurodiversidad_backend.user.Role;
import com.neurodiversidad.neurodiversidad_backend.user.User;

@Component
public class UserAdminMapper {

    public UserAdministrationDTO toDto(User
    		entity) {
        if (entity == null) return null;

        List<String> roleNames = entity.getRoles() == null
                ? List.of()
                : entity.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());

        return UserAdministrationDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .enabled(Boolean.TRUE.equals(entity.isEnabled()))
                .roles(roleNames)
                .lastLoginAt(entity.getLastLoginAt())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .deletedAt(entity.getDeletedAt())
                .deletedBy(entity.getDeletedBy())
                .build();
    }
}
