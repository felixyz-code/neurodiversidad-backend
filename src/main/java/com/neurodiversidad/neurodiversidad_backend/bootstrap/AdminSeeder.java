package com.neurodiversidad.neurodiversidad_backend.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

	private final JdbcTemplate jdbc;
	private final PasswordEncoder passwordEncoder;

	public AdminSeeder(JdbcTemplate jdbc, PasswordEncoder passwordEncoder) {
		this.jdbc = jdbc;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {

		Integer exists = jdbc.queryForObject(
				"SELECT COUNT(*) FROM users WHERE lower(username) = lower(?) AND deleted_at IS NULL", Integer.class,
				"admin");

		if (exists != null && exists > 0) {
			System.out.println("AdminSeeder: usuario admin ya existe.");
			return;
		}

		System.out.println("AdminSeeder: creando usuario admin...");

		String userId = jdbc.queryForObject("SELECT gen_random_uuid()", String.class);
		String encodedPassword = passwordEncoder.encode("admin");

		jdbc.update("""
				INSERT INTO users (id, name, email, username, password_hash, enabled, created_at)
				VALUES (CAST(? AS uuid), ?, ?, ?, ?, TRUE, NOW())
				""", userId, "Administrador del Sistema", "admin@example.com", "admin", encodedPassword);

		String roleId = jdbc.queryForObject("SELECT id FROM roles WHERE name = 'ROLE_DIRECTOR_GENERAL' LIMIT 1",
				String.class);

		jdbc.update("""
				INSERT INTO user_roles (user_id, role_id)
				VALUES (CAST(? AS uuid), CAST(? AS uuid))
				""", userId, roleId);

		System.out.println("AdminSeeder: usuario admin creado → username: admin / password: admin");
	}
}
