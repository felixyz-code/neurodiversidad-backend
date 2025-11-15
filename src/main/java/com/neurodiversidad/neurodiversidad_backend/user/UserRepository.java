package com.neurodiversidad.neurodiversidad_backend.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

	Optional<User> findByUsernameIgnoreCaseAndDeletedAtIsNull(String username);

	boolean existsByUsernameIgnoreCaseAndDeletedAtIsNull(String username);

	boolean existsByEmailIgnoreCaseAndDeletedAtIsNull(String email);
}
