package com.neurodiversidad.neurodiversidad_backend.appointment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    Optional<Appointment> findByIdAndDeletedAtIsNull(UUID id);

    List<Appointment> findBySpecialistIdAndStartAtBetweenAndDeletedAtIsNull(
            UUID specialistId,
            OffsetDateTime from,
            OffsetDateTime to
    );

    @Query("""
            SELECT a
            FROM Appointment a
            JOIN a.patient p
            JOIN a.specialist s
            WHERE a.deletedAt IS NULL
              AND a.startAt >= :from
              AND a.startAt <= :to
              AND (:status IS NULL OR a.status = :status)
              AND (:search IS NULL OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (
                   :filterBySpecialist = false
                   OR (s.id IN :specialistIds)
              )
            """)
    Page<Appointment> searchAppointments(
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            @Param("status") String status,
            @Param("search") String search,
            @Param("specialistIds") List<UUID> specialistIds,
            @Param("filterBySpecialist") boolean filterBySpecialist,
            Pageable pageable
    );

    @Query("""
            SELECT a
            FROM Appointment a
            JOIN a.patient p
            JOIN a.specialist s
            WHERE a.deletedAt IS NULL
              AND a.startAt >= :from
              AND a.startAt <= :to
              AND (:status IS NULL OR a.status = :status)
              AND (:search IS NULL OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (
                   :filterBySpecialist = false
                   OR (s.id IN :specialistIds)
              )
            ORDER BY
              CASE a.status
                WHEN 'PENDING' THEN 1
                WHEN 'CONFIRMED' THEN 2
                WHEN 'COMPLETED' THEN 3
                WHEN 'CANCELED' THEN 4
                ELSE 99
              END ASC,
              a.startAt ASC
            """)
    Page<Appointment> searchAppointmentsStatusOrderAsc(
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            @Param("status") String status,
            @Param("search") String search,
            @Param("specialistIds") List<UUID> specialistIds,
            @Param("filterBySpecialist") boolean filterBySpecialist,
            Pageable pageable
    );

    @Query("""
            SELECT a
            FROM Appointment a
            JOIN a.patient p
            JOIN a.specialist s
            WHERE a.deletedAt IS NULL
              AND a.startAt >= :from
              AND a.startAt <= :to
              AND (:status IS NULL OR a.status = :status)
              AND (:search IS NULL OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (
                   :filterBySpecialist = false
                   OR (s.id IN :specialistIds)
              )
            ORDER BY
              CASE a.status
                WHEN 'PENDING' THEN 1
                WHEN 'CONFIRMED' THEN 2
                WHEN 'COMPLETED' THEN 3
                WHEN 'CANCELED' THEN 4
                ELSE 99
              END DESC,
              a.startAt ASC
            """)
    Page<Appointment> searchAppointmentsStatusOrderDesc(
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            @Param("status") String status,
            @Param("search") String search,
            @Param("specialistIds") List<UUID> specialistIds,
            @Param("filterBySpecialist") boolean filterBySpecialist,
            Pageable pageable
    );

    @Query("""
            SELECT a
            FROM Appointment a
            JOIN a.patient p
            JOIN a.specialist s
            WHERE a.deletedAt IS NULL
              AND a.startAt >= :from
              AND a.startAt <= :to
              AND (:status IS NULL OR a.status = :status)
              AND (
                   :filterBySpecialist = false
                   OR (s.id IN :specialistIds)
              )
            """)
    Page<Appointment> searchAppointmentsWithoutSearch(
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            @Param("status") String status,
            @Param("specialistIds") List<UUID> specialistIds,
            @Param("filterBySpecialist") boolean filterBySpecialist,
            Pageable pageable
    );

    @Query("""
            SELECT a
            FROM Appointment a
            JOIN a.patient p
            JOIN a.specialist s
            WHERE a.deletedAt IS NULL
              AND a.startAt >= :from
              AND a.startAt <= :to
              AND (:status IS NULL OR a.status = :status)
              AND (
                   :filterBySpecialist = false
                   OR (s.id IN :specialistIds)
              )
            ORDER BY
              CASE a.status
                WHEN 'PENDING' THEN 1
                WHEN 'CONFIRMED' THEN 2
                WHEN 'COMPLETED' THEN 3
                WHEN 'CANCELED' THEN 4
                ELSE 99
              END ASC,
              a.startAt ASC
            """)
    Page<Appointment> searchAppointmentsStatusOrderAscWithoutSearch(
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            @Param("status") String status,
            @Param("specialistIds") List<UUID> specialistIds,
            @Param("filterBySpecialist") boolean filterBySpecialist,
            Pageable pageable
    );

    @Query("""
            SELECT a
            FROM Appointment a
            JOIN a.patient p
            JOIN a.specialist s
            WHERE a.deletedAt IS NULL
              AND a.startAt >= :from
              AND a.startAt <= :to
              AND (:status IS NULL OR a.status = :status)
              AND (
                   :filterBySpecialist = false
                   OR (s.id IN :specialistIds)
              )
            ORDER BY
              CASE a.status
                WHEN 'PENDING' THEN 1
                WHEN 'CONFIRMED' THEN 2
                WHEN 'COMPLETED' THEN 3
                WHEN 'CANCELED' THEN 4
                ELSE 99
              END DESC,
              a.startAt ASC
            """)
    Page<Appointment> searchAppointmentsStatusOrderDescWithoutSearch(
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            @Param("status") String status,
            @Param("specialistIds") List<UUID> specialistIds,
            @Param("filterBySpecialist") boolean filterBySpecialist,
            Pageable pageable
    );
}
