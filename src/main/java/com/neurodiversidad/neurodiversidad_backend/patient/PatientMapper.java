package com.neurodiversidad.neurodiversidad_backend.patient;

import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public PatientDto toDto(Patient patient) {
        if (patient == null) return null;

        return PatientDto.builder()
                .id(patient.getId())
                .fullName(patient.getFullName())
                .birthDate(patient.getBirthDate())
                .phone(patient.getPhone())
                .email(patient.getEmail())
                .notes(patient.getNotes())
                .createdAt(patient.getCreatedAt())
                .createdBy(patient.getCreatedBy())
                .updatedAt(patient.getUpdatedAt())
                .updatedBy(patient.getUpdatedBy())
                .build();
    }
}
