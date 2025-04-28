package ru.practicum.dto.comment;

import lombok.Builder;
import lombok.Data;
import ru.practicum.enums.CommentState;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentFullDto {
	private Long id;
	private String text;
	private Long eventId;
	private Long authorId;
	private LocalDateTime created;
	private CommentState state;
}