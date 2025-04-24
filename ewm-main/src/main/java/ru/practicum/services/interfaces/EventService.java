package ru.practicum.services.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.dto.event.*;
import ru.practicum.enums.EventSortType;
import ru.practicum.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
	EventFullDto createEvent(Long userId, NewEventDto newEventDto);

	EventFullDto getEvent(Long eventId, HttpServletRequest request);

	EventFullDto getUserEvent(Long userId, Long eventId);

	List<EventShortDto> getUserEvents(Long userId, Pageable pageable);

	List<EventShortDto> publicSearchEvents(
			String text,
			List<Long> categories,
			Boolean paid,
			LocalDateTime rangeStart,
			LocalDateTime rangeEnd,
			Boolean onlyAvailable,
			EventSortType sort,
			Pageable pageable,
			HttpServletRequest request
	);

	List<EventFullDto> adminSearchEvents(
			List<Long> users,
			List<EventState> states,
			List<Long> categories,
			LocalDateTime rangeStart,
			LocalDateTime rangeEnd,
			Pageable pageable
	);

	EventFullDto updateAdminEvent(Long id, UpdateEventAdminRequest updateEventAdminRequest);

	EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);
}