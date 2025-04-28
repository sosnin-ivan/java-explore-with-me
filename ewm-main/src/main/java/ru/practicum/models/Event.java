package ru.practicum.models;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.enums.EventState;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "initiator_id", nullable = false)
	private User initiator;

	@ManyToOne
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@ManyToOne
	@JoinColumn(name = "location_id", nullable = false)
	private Location location;

	@Builder.Default
	@Column(nullable = false, length = 30)
	@Enumerated(EnumType.STRING)
	private EventState state = EventState.PENDING;

	@Column(nullable = false, length = 120)
	private String title;

	@Column(nullable = false, length = 2000)
	private String annotation;

	@Column(length = 7000)
	private String description;

	@Builder.Default
	@Column(nullable = false)
	private Boolean paid = false;

	@Builder.Default
	@Column(nullable = false)
	private Boolean requestModeration = true;

	@Builder.Default
	@Column(nullable = false)
	private Long participantLimit = 0L;

	@Builder.Default
	@Column(nullable = false)
	private LocalDateTime createdOn = LocalDateTime.now();

	@Column
	private LocalDateTime publishedOn;

	@Column(nullable = false)
	private LocalDateTime eventDate;
}