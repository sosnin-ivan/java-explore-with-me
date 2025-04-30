package ru.practicum.controllers.pub;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.services.interfaces.CommentService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController("publicCommentsController")
@RequestMapping("/comments")
public class CommentsController {
	private final CommentService commentService;

	@GetMapping()
	public ResponseEntity<List<CommentShortDto>> getAllEventComments(
			@RequestParam @Positive Long eventId,
			@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
			@RequestParam(defaultValue = "10") @Positive Integer size
	) {
		log.info("Pub/CommentsController.getAllEventComments: eventId: {}", eventId);
		List<CommentShortDto> comments = commentService.getAllEventComments(eventId, PageRequest.of(from / size, size));
		return new ResponseEntity<>(comments, HttpStatus.OK);
	}
}