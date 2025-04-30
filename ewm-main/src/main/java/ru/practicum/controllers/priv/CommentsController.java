package ru.practicum.controllers.priv;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.services.interfaces.CommentService;

@Slf4j
@RequiredArgsConstructor
@RestController("privateCommentsController")
@RequestMapping("/users/{authorId}/comments")
public class CommentsController {
	private final CommentService commentService;

	@PostMapping
	public ResponseEntity<CommentFullDto> addComment(
			@RequestBody @Valid NewCommentDto newCommentDto,
			@PathVariable @Positive Long authorId
	) {
		log.info("Private/CommentsController.addComment: authorId = {}, newCommentDto = {}", authorId, newCommentDto);
		CommentFullDto comment = commentService.createComment(newCommentDto, authorId);
		return new ResponseEntity<>(comment, HttpStatus.CREATED);
	}

	@PatchMapping("/{commentId}")
	public ResponseEntity<CommentFullDto> updateComment(
			@RequestBody @Valid UpdateCommentDto updateCommentDto,
			@PathVariable @Positive Long authorId,
			@PathVariable @Positive Long commentId
	) {
		log.info("Private/CommentsController.updateComment: authorId = {}, commentId = {}, updateCommentDto = {}", authorId, commentId, updateCommentDto);
		CommentFullDto comment = commentService.updateComment(updateCommentDto, authorId, commentId);
		return new ResponseEntity<>(comment, HttpStatus.OK);
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> deleteComment(
			@PathVariable @Positive Long authorId,
			@PathVariable @Positive Long commentId
	) {
		log.info("Private/CommentsController.deleteComment: authorId = {}, commentId = {}", authorId, commentId);
		commentService.deleteComment(authorId, commentId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}