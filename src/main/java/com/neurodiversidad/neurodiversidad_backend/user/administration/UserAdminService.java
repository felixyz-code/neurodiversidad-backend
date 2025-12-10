package com.neurodiversidad.neurodiversidad_backend.user.administration;

import java.util.List;
import java.util.UUID;

public interface UserAdminService {

	UserAdministrationDTO createUser(CreateUserRequest request, UUID currentUserId);

	UserAdministrationDTO updateUser(UUID id, UpdateUserRequest request, UUID currentUserId);

	    void deleteUser(UUID id, UUID currentUserId);

	    void restoreUser(UUID id, UUID currentUserId);

	    UserAdministrationDTO getUserById(UUID id);

	    List<UserAdministrationDTO> searchUsers(String text, Boolean enabled, String roleName);
}
