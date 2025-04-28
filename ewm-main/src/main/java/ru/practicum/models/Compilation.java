package ru.practicum.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "compilations")
public class Compilation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Builder.Default
	@Column(nullable = false)
	private Boolean pinned = false;

	@Column(nullable = false, length = 50)
	private String title;

	@ManyToMany
	@JoinTable(
			name = "compilations_events",
			joinColumns = @JoinColumn(name = "compilation_id"),
			inverseJoinColumns = @JoinColumn(name = "event_id")
	)
	private List<Event> events;
}