package com.neurodiversidad.neurodiversidad_backend.staff;

import com.neurodiversidad.neurodiversidad_backend.user.Role;
import com.neurodiversidad.neurodiversidad_backend.user.RoleRepository;
import com.neurodiversidad.neurodiversidad_backend.user.User;
import com.neurodiversidad.neurodiversidad_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AssistantAdminServiceImpl implements AssistantAdminService {

    private final AssistantRepository assistantRepository;
    private final SpecialistRepository specialistRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AssistantMapper assistantMapper;
    private final SpecialistMapper specialistMapper;

    @Override
    public AssistantDto createAssistant(CreateAssistantRequest request, UUID currentUserId) {
        if (userRepository.existsByUsernameIgnoreCaseAndDeletedAtIsNull(request.getUsername())) {
            throw new IllegalArgumentException("Ya existe un usuario activo con ese username");
        }
        if (userRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(request.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario activo con ese email");
        }
        if (request.getConfirmPassword() != null &&
                !request.getConfirmPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("La confirmacion de contrasena no coincide");
        }

        Boolean enabled = request.getEnabled();
        if (enabled == null) {
            enabled = Boolean.TRUE;
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(enabled);
        user.setCreatedAt(OffsetDateTime.now());
        user.setCreatedBy(currentUserId);

        Role assistantRole = roleRepository.findByName("ROLE_ASISTENTE_ESPECIALISTA")
                .orElseThrow(() -> new IllegalStateException("No existe el rol ROLE_ASISTENTE_ESPECIALISTA en la base"));
        user.setRoles(new HashSet<>(Set.of(assistantRole)));

        user = userRepository.save(user);

        Set<Specialist> specialists = resolveSpecialists(request.getSpecialistIds());

        Assistant assistant = new Assistant();
        assistant.setUser(user);
        assistant.setSpecialists(specialists);

        assistant = assistantRepository.save(assistant);
        return assistantMapper.toDto(assistant);
    }

    @Override
    public AssistantDto updateAssistantSpecialists(UUID assistantId, List<UUID> specialistIds, UUID currentUserId) {
        Assistant assistant = assistantRepository.findById(assistantId)
                .orElseThrow(() -> new IllegalArgumentException("Asistente no encontrado"));

        Set<Specialist> specialists = resolveSpecialists(specialistIds);
        assistant.setSpecialists(specialists);

        assistant = assistantRepository.save(assistant);
        return assistantMapper.toDto(assistant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssistantDto> listAssistants(UUID specialistId) {
        List<Assistant> list = specialistId == null
                ? assistantRepository.findAll()
                : assistantRepository.findBySpecialists_Id(specialistId);
        return list.stream().map(assistantMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AssistantDto getAssistantByUserId(UUID userId) {
        Assistant assistant = assistantRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Asistente no encontrado para el usuario"));
        return assistantMapper.toDto(assistant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialistDto> listSpecialistsForAssistant(UUID assistantId) {
        Assistant assistant = assistantRepository.findById(assistantId)
                .orElseThrow(() -> new IllegalArgumentException("Asistente no encontrado"));
        return assistant.getSpecialists().stream()
                .map(specialistMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssistantDto> listAssistantsForSpecialist(UUID specialistId) {
        List<Assistant> list = assistantRepository.findBySpecialists_Id(specialistId);
        return list.stream().map(assistantMapper::toDto).toList();
    }

    private Set<Specialist> resolveSpecialists(List<UUID> specialistIds) {
        List<Specialist> specialists = specialistRepository.findAllById(specialistIds);
        if (specialists.size() != specialistIds.size()) {
            Set<UUID> found = specialists.stream().map(Specialist::getId).collect(Collectors.toSet());
            List<UUID> missing = specialistIds.stream()
                    .filter(id -> !found.contains(id))
                    .toList();
            throw new IllegalArgumentException("Especialistas no encontrados: " + missing);
        }
        return new HashSet<>(specialists);
    }
}
