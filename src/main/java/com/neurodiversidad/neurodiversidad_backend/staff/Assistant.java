package com.neurodiversidad.neurodiversidad_backend.staff;

import com.neurodiversidad.neurodiversidad_backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
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

	@ManyToMany
	@JoinTable(
			name = "assistant_specialists",
			joinColumns = @JoinColumn(name = "assistant_id"),
			inverseJoinColumns = @JoinColumn(name = "specialist_id")
	)
	private Set<Specialist> specialists = new HashSet<>();
}
