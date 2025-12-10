package com.neurodiversidad.neurodiversidad_backend.user.administration;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neurodiversidad.neurodiversidad_backend.user.Role;
import com.neurodiversidad.neurodiversidad_backend.user.RoleRepository;
import com.neurodiversidad.neurodiversidad_backend.user.User;
import com.neurodiversidad.neurodiversidad_backend.user.UserRepository;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAdminServiceImpl implements UserAdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserAdminMapper userAdminMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserAdministrationDTO createUser(CreateUserRequest request, UUID currentUserId) {
        // Validar unicidad básica desde servicio (además de constraints DB).
        if (userRepository.existsByUsernameIgnoreCaseAndDeletedAtIsNull(request.getUsername())) {
            throw new IllegalArgumentException("Ya existe un usuario activo con ese username");
        }
        if (userRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(request.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario activo con ese email");
        }

        // (Opcional) validar confirmación de contraseña
        if (request.getConfirmPassword() != null &&
            !request.getConfirmPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("La confirmación de contraseña no coincide");
        }

        Boolean enabled = request.getEnabled();
        if (enabled == null) {
            enabled = Boolean.TRUE;
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());

        // 👇 AQUÍ se encripta SIEMPRE
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        user.setEnabled(enabled);
        user.setCreatedAt(OffsetDateTime.now());
        user.setCreatedBy(currentUserId);

        // Roles
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            var roles = new HashSet<Role>();
            for (String roleName : request.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + roleName));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        user = userRepository.save(user);
        return userAdminMapper.toDto(user);
    }

    @Override
    public UserAdministrationDTO updateUser(UUID id, UpdateUserRequest request, UUID currentUserId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Solo actualizamos campos que vienen no null

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }

        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        }

        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        if (request.getRoles() != null) {
            var roles = new HashSet<Role>();
            for (String roleName : request.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + roleName));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        user.setUpdatedAt(OffsetDateTime.now());
        user.setUpdatedBy(currentUserId);

        user = userRepository.save(user);
        return userAdminMapper.toDto(user);
    }

    @Override
    public void deleteUser(UUID id, UUID currentUserId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setEnabled(false);
        user.setDeletedAt(OffsetDateTime.now());
        user.setDeletedBy(currentUserId);
        user.setUpdatedAt(OffsetDateTime.now());
        user.setUpdatedBy(currentUserId);

        userRepository.save(user);
    }

    @Override
    public void restoreUser(UUID id, UUID currentUserId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setDeletedAt(null);
        user.setDeletedBy(null);
        user.setEnabled(true);

        user.setUpdatedAt(OffsetDateTime.now());
        user.setUpdatedBy(currentUserId);

        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserAdministrationDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return userAdminMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAdministrationDTO> searchUsers(String text, Boolean enabled, String roleName) {
        List<User> users = userRepository.search(
                (text != null && !text.isBlank()) ? text : null,
                enabled,
                (roleName != null && !roleName.isBlank()) ? roleName : null
        );
        return users.stream().map(userAdminMapper::toDto).toList();
    }
}
