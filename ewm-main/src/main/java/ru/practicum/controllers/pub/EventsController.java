package ru.practicum.controllers.pub;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.enums.EventSortType;
import ru.practicum.services.interfaces.EventService;
import ru.practicum.utils.Constants;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController("publicEventsController")
@RequestMapping("/events")
public class EventsController {
	private final EventService eventService;

	@GetMapping("/{eventId}")
	public ResponseEntity<EventFullDto> getEvent(
			@PathVariable Long eventId,
			HttpServletRequest request
	) {
		log.info("Pub/EventsController.getEvent: eventId: {}", eventId);
		EventFullDto event = eventService.getEvent(eventId, request);
		return new ResponseEntity<>(event, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<EventShortDto>> getAllPublicEvents(
			@RequestParam(required = false) String text,
			@RequestParam(required = false) List<Long> categories,
			@RequestParam(required = false) Boolean paid,
			@RequestParam(required = false) @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT) LocalDateTime rangeStart,
			@RequestParam(required = false) @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT) LocalDateTime rangeEnd,
			@RequestParam(defaultValue = "false") Boolean onlyAvailable,
			@RequestParam(defaultValue = "EVENT_DATE") EventSortType sort,
			@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
			@RequestParam(defaultValue = "10") @Positive Integer size,
			HttpServletRequest request
	) {
		log.info("Pub/EventsController.getAllPublicEvents: text: {}, sort: {}", text, sort);
		List<EventShortDto> foundedEvents = eventService.publicSearchEvents(
				text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
				PageRequest.of(from / size, size), request
		);
		return new ResponseEntity<>(foundedEvents, HttpStatus.OK);
	}
}