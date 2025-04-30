package ru.practicum.dto.comment;

import lombok.Builder;
import lombok.Data;
import ru.practicum.dto.user.UserShortDto;

@Data
@Builder
public class CommentShortDto {
	private String text;
	private UserShortDto author;
}