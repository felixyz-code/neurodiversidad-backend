package com.neurodiversidad.neurodiversidad_backend.recruitment;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;

public interface RecruitmentService {

	RecruitmentDto create(CreateRecruitmentRequest request, UUID currentUserId);

	RecruitmentDto update(UUID id, UpdateRecruitmentRequest request, UUID currentUserId);

	RecruitmentDto getById(UUID id);

	Page<RecruitmentDto> search(
			String status,
			String text,
			RecruitmentServiceType tipoServicio,
			RecruitmentStatus estatus,
			LocalDate from,
			LocalDate to,
			java.util.List<String> sort,
			int page,
			int size
	);

	void delete(UUID id, UUID currentUserId);

	void restore(UUID id, UUID currentUserId);
}
