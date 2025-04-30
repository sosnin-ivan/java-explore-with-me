package ru.practicum.dto.comment;

import lombok.Data;
import ru.practicum.enums.CommentState;

@Data
public class UpdateCommentStateDto {
	private CommentState state;
}