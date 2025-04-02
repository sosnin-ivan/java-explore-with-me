package ru.practicum.hit;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hits")
public class Hit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;

	@Column(nullable = false)
	private String app;

	@Column(nullable = false)
	private String uri;

	@Column(nullable = false)
	private String ip;

	@Column(nullable = false)
	private LocalDateTime timestamp;
}