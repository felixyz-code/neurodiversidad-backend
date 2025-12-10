package com.neurodiversidad.neurodiversidad_backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

	Optional<Role> findByName(String name);
	
    List<Role> findByNameIn(Set<String> names);
    
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
    List<User> search(String text, Boolean enabled, String roleName);

}
