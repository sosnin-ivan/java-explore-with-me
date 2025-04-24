package ru.practicum.controllers.admin;

import jakarta.validation.Valid;
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
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.enums.EventState;
import ru.practicum.services.interfaces.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController("adminEventsController")
@RequestMapping("/admin/events")
public class EventsController {
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private final EventService eventService;

	@GetMapping
	public ResponseEntity<List<EventFullDto>> searchEvents(
			@RequestParam(required = false) List<Long> users,
			@RequestParam(required = false) List<EventState> states,
			@RequestParam(required = false) List<Long> categories,
			@RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeStart,
			@RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeEnd,
			@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
			@RequestParam(defaultValue = "10") @Positive Integer size
	) {
		log.info("Admin/EventsController.adminSearchEvents: users: {}, states: {}, categories: {}, rangeStart: {}, rangeEnd: {}, from: {}, size: {}",
				users, states, categories, rangeStart, rangeEnd, from, size);
		List<EventFullDto> foundedEvents = eventService.adminSearchEvents(
				users, states, categories, rangeStart, rangeEnd, PageRequest.of(from / size, size)
		);
		return new ResponseEntity<>(foundedEvents, HttpStatus.OK);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<EventFullDto> updateEvent(
			@PathVariable @Positive Long id,
			@RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest
	) {
		log.info("Admin/EventsController.updateEvent: id: {}, updateEventAdminRequest: {}", id, updateEventAdminRequest);
		EventFullDto updatedEvent = eventService.updateAdminEvent(id, updateEventAdminRequest);
		return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
	}
}