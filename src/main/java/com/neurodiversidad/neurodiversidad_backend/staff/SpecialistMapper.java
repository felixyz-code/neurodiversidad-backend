package com.neurodiversidad.neurodiversidad_backend.staff;

import org.springframework.stereotype.Component;

@Component
public class SpecialistMapper {

    public SpecialistDto toDto(Specialist entity) {
        if (entity == null) {
            return null;
        }

        return SpecialistDto.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .userName(entity.getUser().getName())
                .specialty(entity.getSpecialty())
                .build();
    }
}
