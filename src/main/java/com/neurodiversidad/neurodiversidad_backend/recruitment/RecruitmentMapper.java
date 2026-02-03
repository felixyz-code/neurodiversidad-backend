package com.neurodiversidad.neurodiversidad_backend.recruitment;

public class RecruitmentMapper {

	public RecruitmentDto toDto(Recruitment entity) {
		return RecruitmentDto.builder()
				.id(entity.getId())
				.nombre(entity.getNombre())
				.tipoServicio(entity.getTipoServicio())
				.fechaInicio(entity.getFechaInicio())
				.fechaSalida(entity.getFechaSalida())
				.estatus(entity.getEstatus())
				.createdAt(entity.getCreatedAt())
				.createdBy(entity.getCreatedBy())
				.updatedAt(entity.getUpdatedAt())
				.updatedBy(entity.getUpdatedBy())
				.deletedAt(entity.getDeletedAt())
				.build();
	}
}
