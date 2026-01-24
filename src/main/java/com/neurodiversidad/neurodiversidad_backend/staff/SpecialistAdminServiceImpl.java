package com.neurodiversidad.neurodiversidad_backend.staff;

import com.neurodiversidad.neurodiversidad_backend.user.Role;
import com.neurodiversidad.neurodiversidad_backend.user.RoleRepository;
import com.neurodiversidad.neurodiversidad_backend.user.User;
import com.neurodiversidad.neurodiversidad_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SpecialistAdminServiceImpl implements SpecialistAdminService {

    private final SpecialistRepository specialistRepository;
    private final AssistantRepository assistantRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SpecialistMapper specialistMapper;

    @Override
    public SpecialistDto createSpecialist(CreateSpecialistRequest request, UUID currentUserId) {

        // 1) Validar que el usuario exista
        User user = userRepository.findByIdAndDeletedAtIsNull(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // 2) Validar que no tenga ya un Specialist asociado
        specialistRepository.findByUserId(user.getId()).ifPresent(s -> {
            throw new IllegalArgumentException("Este usuario ya está registrado como especialista");
        });

        // 3) Normalizar especialidad
        String specialty = request.getSpecialty().toUpperCase();
        switch (specialty) {
            case "PSICOLOGIA":
            case "PEDAGOGIA":
            case "FISIOTERAPIA":
            case "PEDIATRIA":
            case "OTRA":
                break;
            default:
                throw new IllegalArgumentException("Especialidad no válida: " + specialty);
        }

        // 4) Crear Specialist
        Specialist specialist = new Specialist();
        specialist.setUser(user);
        specialist.setSpecialty(specialty);

        specialist = specialistRepository.save(specialist);

        // 5) Asegurar que el usuario tenga el rol ROLE_ESPECIALISTA
        Role specialistRole = roleRepository.findByName("ROLE_ESPECIALISTA")
                .orElseThrow(() -> new IllegalStateException("No existe el rol ROLE_ESPECIALISTA en la base"));

        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        if (!user.getRoles().contains(specialistRole)) {
            user.getRoles().add(specialistRole);
            user.setUpdatedAt(OffsetDateTime.now());
            user.setUpdatedBy(currentUserId);
            userRepository.save(user);
        }

        return specialistMapper.toDto(specialist);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialistDto> searchSpecialists(String specialty, String name) {
        List<Specialist> list;

        if (specialty != null && !specialty.isBlank()) {
            list = specialistRepository.findBySpecialty(specialty.toUpperCase());
        } else if (name != null && !name.isBlank()) {
            list = specialistRepository.findByUser_NameContainingIgnoreCase(name);
        } else {
            list = specialistRepository.findAll();
        }

        return list.stream().map(specialistMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialistDto getById(UUID id) {
        Specialist specialist = specialistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Especialista no encontrado"));
        return specialistMapper.toDto(specialist);
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialistDto getByUserId(UUID userId) {
        Specialist specialist = specialistRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Especialista no encontrado para el usuario"));
        return specialistMapper.toDto(specialist);
    }

    @Override
    public void updateAssistants(UUID specialistId, List<UUID> assistantIds) {
        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new IllegalArgumentException("Especialista no encontrado"));

        List<Assistant> current = assistantRepository.findBySpecialists_Id(specialistId);
        List<Assistant> desired = assistantRepository.findAllById(assistantIds);

        if (desired.size() != assistantIds.size()) {
            throw new IllegalArgumentException("Algunos asistentes no existen");
        }

        for (Assistant assistant : current) {
            if (!assistantIds.contains(assistant.getId())) {
                assistant.getSpecialists().remove(specialist);
            }
        }

        for (Assistant assistant : desired) {
            assistant.getSpecialists().add(specialist);
        }

        assistantRepository.saveAll(current);
        assistantRepository.saveAll(desired);
    }
}
