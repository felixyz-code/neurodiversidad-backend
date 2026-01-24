package com.neurodiversidad.neurodiversidad_backend.staff;

import java.util.List;
import java.util.UUID;

public interface SpecialistAdminService {

	SpecialistDto createSpecialist(CreateSpecialistRequest request, UUID currentUserId);

	List<SpecialistDto> searchSpecialists(String specialty, String name);

	SpecialistDto getById(UUID id);

	SpecialistDto getByUserId(UUID userId);

	void updateAssistants(UUID specialistId, List<UUID> assistantIds);
}
