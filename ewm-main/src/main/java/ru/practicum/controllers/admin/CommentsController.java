package ru.practicum.controllers.admin;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.UpdateCommentStateDto;
import ru.practicum.services.interfaces.CommentService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController("adminCommentsController")
@RequestMapping("/admin/comments")
public class CommentsController {
	private final CommentService commentService;

	@GetMapping("/users/{authorId}")
	public ResponseEntity<List<CommentFullDto>> getCommentsByAuthor(
			@PathVariable @Positive Long authorId,
			@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
			@RequestParam(defaultValue = "10") @Positive Integer size
	) {
		log.info("Admin/CommentsController.getCommentsByAuthor: authorId: {}", authorId);
		List<CommentFullDto> comments = commentService.getCommentsByAuthor(authorId, PageRequest.of(from / size, size));
		return new ResponseEntity<>(comments, HttpStatus.OK);
	}

	@PatchMapping("/{commentId}")
	public ResponseEntity<CommentFullDto> updateState(
			@PathVariable @Positive Long commentId,
			@RequestBody UpdateCommentStateDto updateCommentStateDto
	) {
		log.info("Admin/CommentsController.updateState: commentId: {}, state: {}", commentId, updateCommentStateDto);
		CommentFullDto adminCommentDto = commentService.updateState(commentId, updateCommentStateDto.getState());
		return new ResponseEntity<>(adminCommentDto, HttpStatus.OK);
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> deleteComment(
			@PathVariable @Positive Long commentId
	) {
		log.info("Admin/CommentsController.deleteComment: id: {}", commentId);
		commentService.deleteComment(commentId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}