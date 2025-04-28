package ru.practicum.dto.comment;

import lombok.Builder;
import lombok.Data;
import ru.practicum.models.User;

@Data
@Builder
public class CommentShortDto {
	private String text;
	private User author;
}