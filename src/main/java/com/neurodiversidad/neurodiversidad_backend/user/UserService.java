package com.neurodiversidad.neurodiversidad_backend.user;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserDTO> findUsers(String usernameFilter) {
        List<User> users;
        if (usernameFilter == null || usernameFilter.isBlank()) {
            users = userRepository.findAllByDeletedAtIsNull();
        } else {
            users = userRepository
                    .findByUsernameContainingIgnoreCaseAndDeletedAtIsNull(usernameFilter);
        }
        return users.stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    public UserDTO createUser(UserCreateRequest request, UUID createdBy) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        user.setCreatedBy(createdBy);

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            var roles = roleRepository.findByNameIn(request.getRoles());
            user.setRoles(Set.copyOf(roles));
        }

        User saved = userRepository.save(user);
        return UserMapper.toDTO(saved);
    }

    public UserDTO updateUser(UUID id, UserUpdateRequest request, UUID modifiedBy) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setEnabled(request.isEnabled());
        user.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        user.setUpdatedBy(modifiedBy);

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            var roles = roleRepository.findByNameIn(request.getRoles());
            user.setRoles(Set.copyOf(roles));
        }

        User saved = userRepository.save(user);
        return UserMapper.toDTO(saved);
    }

    public void deleteUser(UUID id, UUID deletedBy) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));

        user.setDeletedAt(OffsetDateTime.now(ZoneOffset.UTC));
        user.setDeletedBy(deletedBy);
        userRepository.save(user);
    }
}
