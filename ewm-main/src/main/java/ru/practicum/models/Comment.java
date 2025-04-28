package ru.practicum.models;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.enums.CommentState;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false, length = 1000)
	private String text;

	@ManyToOne
	@JoinColumn(name = "event_id", nullable = false)
	private Event event;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User author;

	@Builder.Default
	@Column
	private LocalDateTime created = LocalDateTime.now();

	@Builder.Default
	@Column(nullable = false, length = 30)
	@Enumerated(EnumType.STRING)
	private CommentState state = CommentState.PENDING;
}