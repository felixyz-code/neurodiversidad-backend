package com.neurodiversidad.neurodiversidad_backend.appointment;

import com.neurodiversidad.neurodiversidad_backend.patient.Patient;
import com.neurodiversidad.neurodiversidad_backend.staff.Specialist;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

	@Id
	@GeneratedValue
	@UuidGenerator
	private UUID id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "specialist_id", nullable = false)
	private Specialist specialist;

	@ManyToOne(optional = false)
	@JoinColumn(name = "patient_id", nullable = false)
	private Patient patient;

	@Column(name = "start_at", nullable = false)
	private OffsetDateTime startAt;

	@Column(name = "duration_minutes", nullable = false)
	private Integer durationMinutes;

	@Column(nullable = false)
	private String status; // PENDING, CONFIRMED, COMPLETED, CANCELED

	private String notes;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt = OffsetDateTime.now();

	@Column(name = "created_by")
	private UUID createdBy;

	@Column(name = "updated_at")
	private OffsetDateTime updatedAt;

	@Column(name = "updated_by")
	private UUID updatedBy;

	@Column(name = "deleted_at")
	private OffsetDateTime deletedAt;

	public OffsetDateTime getEndAt() {
		return startAt != null && durationMinutes != null ? startAt.plusMinutes(durationMinutes) : null;
	}
}
