package com.neurodiversidad.neurodiversidad_backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

	Optional<User> findByUsernameIgnoreCaseAndDeletedAtIsNull(String username);

	List<User> findByUsernameContainingIgnoreCaseAndDeletedAtIsNull(String username);

	List<User> findAllByDeletedAtIsNull();

	boolean existsByUsernameIgnoreCaseAndDeletedAtIsNull(String username);

	boolean existsByEmailIgnoreCaseAndDeletedAtIsNull(String email);

	Optional<User> findByIdAndDeletedAtIsNull(UUID id);

	@Query("""
			SELECT DISTINCT u
			FROM User u
			LEFT JOIN u.roles r
			WHERE u.deletedAt IS NULL
			  AND (:enabled IS NULL OR u.enabled = :enabled)
			  AND (:roleName IS NULL OR r.name = :roleName)
			  AND (
			       :text IS NULL
			       OR LOWER(u.name) LIKE LOWER(CONCAT('%', :text, '%'))
			       OR LOWER(u.email) LIKE LOWER(CONCAT('%', :text, '%'))
			       OR LOWER(u.username) LIKE LOWER(CONCAT('%', :text, '%'))
			  )
			ORDER BY u.name ASC
			""")
	List<User> search(@Param("text") String text, @Param("enabled") Boolean enabled,
			@Param("roleName") String roleName);
}
