package ru.practicum.controllers.priv;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.services.interfaces.EventService;
import ru.practicum.services.interfaces.RequestService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController("privateEventsController")
@RequestMapping("/users/{userId}/events")
public class EventsController {
	private final EventService eventService;
	private final RequestService requestService;

	@PostMapping
	public ResponseEntity<EventFullDto> createEvent(
			@PathVariable @Positive Long userId,
			@RequestBody @Valid NewEventDto newEventDto
	) {
		log.info("Private/EventsController.createEvent: userId: {}, newEventDto: {}", userId, newEventDto);
		EventFullDto eventFullDto = eventService.createEvent(userId, newEventDto);
		return new ResponseEntity<>(eventFullDto, HttpStatus.CREATED);
	}

	@GetMapping("/{eventId}")
	public ResponseEntity<EventFullDto> getUserEvent(
			@PathVariable @Positive Long userId,
			@PathVariable @Positive Long eventId
	) {
		log.info("Private/EventsController.getUserEvent: userId: {}, eventId: {}", userId, eventId);
		EventFullDto eventFullDto = eventService.getUserEvent(userId, eventId);
		return new ResponseEntity<>(eventFullDto, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<EventShortDto>> getUserEvents(
			@PathVariable @Positive Long userId,
			@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
			@RequestParam(defaultValue = "10") @Positive Integer size
	) {
		log.info("Private/EventsController.getUserEvents: userId: {}", userId);
		List<EventShortDto> list = eventService.getUserEvents(userId, PageRequest.of(from / size, size));
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@PatchMapping("/{eventId}")
	public ResponseEntity<EventFullDto> updateUserEvent(
			@PathVariable @Positive Long userId,
			@PathVariable @Positive Long eventId,
			@RequestBody @Valid UpdateEventUserRequest updateEventUserRequest
	) {
		log.info("Private/EventsController.updateUserEvent: userId: {}, UpdateEventUserRequest: {}", userId, updateEventUserRequest);
		EventFullDto eventFullDto = eventService.updateUserEvent(userId, eventId, updateEventUserRequest);
		return new ResponseEntity<>(eventFullDto, HttpStatus.OK);
	}

	@GetMapping("/{eventId}/requests")
	public ResponseEntity<List<ParticipationRequestDto>> getEventRequests(
			@PathVariable @Positive Long userId,
			@PathVariable @Positive Long eventId
	) {
		log.info("Private/EventsController.getEventRequests: userId: {}, eventId: {}", userId, eventId);
		List<ParticipationRequestDto> requests = requestService.getEventRequests(userId, eventId);
		return new ResponseEntity<>(requests, HttpStatus.OK);
	}

	@PatchMapping({"/{eventId}/requests"})
	public ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatus(
			@PathVariable @Positive Long userId,
			@PathVariable @Positive Long eventId,
			@RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest
	) {
		log.info("Private/EventsController.updateRequestStatus: userId: {}, eventId: {}", userId, eventId);
		EventRequestStatusUpdateResult result = requestService.updateRequestStatus(userId, eventId, eventRequestStatusUpdateRequest);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}