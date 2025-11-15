package com.neurodiversidad.neurodiversidad_backend.staff;

import com.neurodiversidad.neurodiversidad_backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "assistants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assistant {

	@Id
	@GeneratedValue
	@UuidGenerator
	private UUID id;

	@OneToOne(optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "specialist_id", nullable = false)
	private Specialist specialist;
}
