package ru.practicum.services.interfaces;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.enums.CommentState;

import java.util.List;

public interface CommentService {
	List<CommentFullDto> getCommentsByAuthor(Long authorId, Pageable pageable);

	List<CommentShortDto> getAllEventComments(Long eventId, Pageable pageable);

	CommentFullDto createComment(NewCommentDto newCommentDto, Long authorId);

	CommentFullDto updateComment(UpdateCommentDto updateCommentDto, Long authorId, Long commentId);

	CommentFullDto updateState(Long commentId, CommentState state);

	void deleteComment(Long authorId, Long commentId);

	void deleteComment(Long commentId);
}