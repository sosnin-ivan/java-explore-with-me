package ru.practicum.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.models.Comment;
import ru.practicum.models.Event;
import ru.practicum.models.User;

@Component
public class CommentMapper {
	public static Comment toComment(NewCommentDto newCommentDto, Event event, User author) {
		if (newCommentDto == null) {
			return null;
		}
		return Comment.builder()
				.text(newCommentDto.getText())
				.event(event)
				.author(author)
				.build();
	}

	public static CommentShortDto toCommentShortDto(Comment comment) {
		if (comment == null) {
			return null;
		}
		return CommentShortDto.builder()
				.text(comment.getText())
				.author(comment.getAuthor())
				.build();
	}

	public static CommentFullDto toCommentFullDto(Comment comment) {
		if (comment == null) {
			return null;
		}
		return CommentFullDto.builder()
				.id(comment.getId())
				.text(comment.getText())
				.eventId(comment.getEvent().getId())
				.authorId(comment.getAuthor().getId())
				.created(comment.getCreated())
				.state(comment.getState())
				.build();
	}
}