package com.neurodiversidad.neurodiversidad_backend.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Crea un usuario administrador inicial únicamente si NO existe.
 * Útil en entorno de desarrollo antes de tener endpoints de creación de usuarios.
 */
@Component
public class AdminSeeder implements CommandLineRunner {

    private final JdbcTemplate jdbc;

    public AdminSeeder(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(String... args) {

        // Verificar si el usuario admin ya existe
        Integer exists = jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE lower(username) = lower(?) AND deleted_at IS NULL",
                Integer.class,
                "admin"
        );

        if (exists != null && exists > 0) {
            System.out.println("AdminSeeder: usuario admin ya existe.");
            return;
        }

        System.out.println("AdminSeeder: creando usuario admin...");

        // Crear UUID para usuario
        String userId = jdbc.queryForObject("SELECT gen_random_uuid()", String.class);

        // (Temporal) sin BCrypt → usamos {noop} para no cifrar hasta tener Security configurado
        jdbc.update("""
                INSERT INTO users (id, name, email, username, password_hash, enabled, created_at)
                VALUES (CAST(? AS uuid), ?, ?, ?, ?, TRUE, NOW())
                """,
                userId,
                "Administrador del Sistema",
                "admin@example.com",
                "admin",
                "{noop}admin"
        );

        // Obtener ID del rol ROLE_DIRECTOR_GENERAL
        String roleId = jdbc.queryForObject(
                "SELECT id FROM roles WHERE name = 'ROLE_DIRECTOR_GENERAL' LIMIT 1",
                String.class
        );

        // Relación user-role
        jdbc.update("""
                INSERT INTO user_roles (user_id, role_id)
                VALUES (CAST(? AS uuid), CAST(? AS uuid))
                """,
                userId,
                roleId
        );

        System.out.println("AdminSeeder: usuario admin creado → username: admin / password: admin");
    }
}
