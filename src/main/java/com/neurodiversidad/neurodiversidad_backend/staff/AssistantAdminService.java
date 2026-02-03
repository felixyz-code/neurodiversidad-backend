package com.neurodiversidad.neurodiversidad_backend.staff;

import java.util.List;
import java.util.UUID;

public interface AssistantAdminService {

    AssistantDto createAssistant(CreateAssistantRequest request, UUID currentUserId);

    AssistantDto updateAssistantSpecialists(UUID assistantId, List<UUID> specialistIds, UUID currentUserId);

    List<AssistantDto> listAssistants(UUID specialistId);

    AssistantDto getAssistantByUserId(UUID userId);

    List<SpecialistDto> listSpecialistsForAssistant(UUID assistantId);

    List<AssistantDto> listAssistantsForSpecialist(UUID specialistId);
}
