package com.neurodiversidad.neurodiversidad_backend.recruitment;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neurodiversidad.neurodiversidad_backend.util.SortUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RecruitmentServiceImpl implements RecruitmentService {

	private final RecruitmentRepository recruitmentRepository;
	private final RecruitmentMapper recruitmentMapper = new RecruitmentMapper();

	@Override
	public RecruitmentDto create(CreateRecruitmentRequest request, UUID currentUserId) {
		Recruitment entity = Recruitment.builder()
				.nombre(request.getNombre())
				.tipoServicio(request.getTipoServicio())
				.fechaInicio(request.getFechaInicio())
				.fechaSalida(request.getFechaSalida())
				.estatus(request.getEstatus())
				.createdAt(OffsetDateTime.now())
				.createdBy(currentUserId)
				.build();

		entity = recruitmentRepository.save(entity);
		return recruitmentMapper.toDto(entity);
	}

	@Override
	public RecruitmentDto update(UUID id, UpdateRecruitmentRequest request, UUID currentUserId) {
		Recruitment entity = recruitmentRepository.findByIdAndDeletedAtIsNull(id)
				.orElseThrow(() -> new IllegalArgumentException("Registro no encontrado con id: " + id));

		if (request.getNombre() != null) {
			entity.setNombre(request.getNombre());
		}
		if (request.getTipoServicio() != null) {
			entity.setTipoServicio(request.getTipoServicio());
		}
		if (request.getFechaInicio() != null) {
			entity.setFechaInicio(request.getFechaInicio());
		}
		if (request.getFechaSalida() != null) {
			entity.setFechaSalida(request.getFechaSalida());
		}
		if (request.getEstatus() != null) {
			entity.setEstatus(request.getEstatus());
		}

		entity.setUpdatedAt(OffsetDateTime.now());
		entity.setUpdatedBy(currentUserId);

		entity = recruitmentRepository.save(entity);
		return recruitmentMapper.toDto(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public RecruitmentDto getById(UUID id) {
		Recruitment entity = recruitmentRepository.findByIdAndDeletedAtIsNull(id)
				.orElseThrow(() -> new IllegalArgumentException("Registro no encontrado con id: " + id));
		return recruitmentMapper.toDto(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<RecruitmentDto> search(String status, String text, RecruitmentServiceType tipoServicio,
			RecruitmentStatus estatus, LocalDate from, LocalDate to, List<String> sort, int page, int size) {

		String normalizedStatus = normalizeStatus(status);
		String normalizedText = (text == null || text.isBlank()) ? null : text;

		Sort defaultSort = Sort.by(
				Sort.Order.desc("fechaInicio"),
				Sort.Order.desc("createdAt")
		);
		Sort sortSpec = SortUtils.parseSort(
				sort,
				Set.of("fechaInicio", "fechaSalida", "nombre", "createdAt", "estatus", "tipoServicio"),
				defaultSort
		);

		Page<Recruitment> result = recruitmentRepository.search(
				normalizedStatus,
				normalizedText,
				tipoServicio,
				estatus,
				from,
				to,
				PageRequest.of(page, size, sortSpec)
		);

		return result.map(recruitmentMapper::toDto);
	}

	@Override
	public void delete(UUID id, UUID currentUserId) {
		Recruitment entity = recruitmentRepository.findByIdAndDeletedAtIsNull(id)
				.orElseThrow(() -> new IllegalArgumentException("Registro no encontrado con id: " + id));

		entity.setDeletedAt(OffsetDateTime.now());
		entity.setUpdatedAt(OffsetDateTime.now());
		entity.setUpdatedBy(currentUserId);
		recruitmentRepository.save(entity);
	}

	@Override
	public void restore(UUID id, UUID currentUserId) {
		Recruitment entity = recruitmentRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Registro no encontrado con id: " + id));

		if (entity.getDeletedAt() == null) {
			throw new IllegalStateException("El registro no esta borrado");
		}

		entity.setDeletedAt(null);
		entity.setUpdatedAt(OffsetDateTime.now());
		entity.setUpdatedBy(currentUserId);
		recruitmentRepository.save(entity);
	}

	private String normalizeStatus(String status) {
		if (status == null || status.isBlank()) {
			return "active";
		}
		String normalized = status.trim().toLowerCase();
		return switch (normalized) {
			case "active", "deleted", "all" -> normalized;
			default -> throw new IllegalArgumentException("status invalido: " + status);
		};
	}
}
