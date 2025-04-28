package ru.practicum.models;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.enums.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requests")
public class Request {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "requester_id", nullable = false)
	private User requester;

	@ManyToOne
	@JoinColumn(name = "event_id", nullable = false)
	private Event event;

	@Builder.Default
	@Column(nullable = false, length = 30)
	@Enumerated(EnumType.STRING)
	private RequestStatus status = RequestStatus.PENDING;

	@Builder.Default
	@Column(nullable = false)
	private LocalDateTime created = LocalDateTime.now();
}