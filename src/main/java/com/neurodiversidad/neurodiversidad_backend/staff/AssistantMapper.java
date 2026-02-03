package com.neurodiversidad.neurodiversidad_backend.staff;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AssistantMapper {

    public AssistantDto toDto(Assistant entity) {
        if (entity == null) {
            return null;
        }

        List<Specialist> specialists = entity.getSpecialists() == null
                ? List.of()
                : entity.getSpecialists().stream().toList();

        return AssistantDto.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .userName(entity.getUser().getName())
                .specialistIds(specialists.stream().map(Specialist::getId).collect(Collectors.toList()))
                .specialistNames(specialists.stream()
                        .map(s -> s.getUser().getName())
                        .collect(Collectors.toList()))
                .build();
    }
}
