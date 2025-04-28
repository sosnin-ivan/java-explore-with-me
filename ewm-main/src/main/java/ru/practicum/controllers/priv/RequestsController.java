package ru.practicum.controllers.priv;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.services.interfaces.RequestService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController("privateRequestsController")
@RequestMapping("/users/{userId}/requests")
public class RequestsController {
	private final RequestService requestService;

	@PostMapping
	public ResponseEntity<ParticipationRequestDto> createRequest(
			@PathVariable @Positive Long userId,
			@RequestParam @Positive Long eventId
	) {
		log.info("Private/RequestsController.createRequest: userId: {}, eventId: {}", userId, eventId);
		ParticipationRequestDto participationRequestDto = requestService.createRequest(userId, eventId);
		return new ResponseEntity<>(participationRequestDto, HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<List<ParticipationRequestDto>> getUserRequests(
			@PathVariable @Positive Long userId
	) {
		log.info("Private/RequestsController.getUserRequests: userId: {}", userId);
		List<ParticipationRequestDto> requests = requestService.getUserRequests(userId);
		return new ResponseEntity<>(requests, HttpStatus.OK);
	}

	@PatchMapping("/{requestId}/cancel")
	public ResponseEntity<ParticipationRequestDto> cancelRequest(
			@PathVariable @Positive Long userId,
			@PathVariable @Positive Long requestId
	) {
		log.info("Private/RequestsController.cancelRequest: userId: {}, requestId: {}", userId, requestId);
		ParticipationRequestDto participationRequestDto = requestService.cancelRequest(userId, requestId);
		return new ResponseEntity<>(participationRequestDto, HttpStatus.OK);
	}
}