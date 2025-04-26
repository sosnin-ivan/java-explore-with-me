package ru.practicum.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.*;
import ru.practicum.enums.EventSortType;
import ru.practicum.enums.EventState;
import ru.practicum.enums.EventStateAction;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.EventMapper;
import ru.practicum.mappers.LocationMapper;
import ru.practicum.models.Category;
import ru.practicum.models.Event;
import ru.practicum.models.Location;
import ru.practicum.models.User;
import ru.practicum.repositories.*;
import ru.practicum.services.interfaces.EventService;
import ru.practicum.utils.EventUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventServiceImpl implements EventService {
	EventRepository eventRepository;
	CategoryRepository categoryRepository;
	UserRepository userRepository;
	LocationRepository locationRepository;
	EventUtils eventUtils;

	@Override
	@Transactional
	public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
		User user = findUser(userId);
		Category category = findCategory(newEventDto.getCategory());
		Location location = locationRepository.save(LocationMapper.toLocation(newEventDto.getLocation()));
		checkNewEvent(newEventDto);
		Event event = EventMapper.toEvent(newEventDto, user, category, location);
		return EventMapper.toEventFullDto(eventRepository.save(event), 0L, 0L);
	}

	@Override
	public EventFullDto getUserEvent(Long userId, Long eventId) {
		findUser(userId);
		Event event = findEvent(eventId);
		Map<Long, Long> confirmedRequests = eventUtils.getConfirmedRequests(List.of(event));
		Map<Long, Long> viewStats = eventUtils.getViews(List.of(event));
		return EventMapper.toEventFullDto(
				event,
				confirmedRequests.getOrDefault(eventId, 0L),
				viewStats.getOrDefault(eventId, 0L)
		);
	}

	@Override
	public List<EventShortDto> getUserEvents(Long userId, Pageable pageable) {
		List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable).getContent();
		Map<Long, Long> confirmedRequests = eventUtils.getConfirmedRequests(events);
		Map<Long, Long> viewStats = eventUtils.getViews(events);
		return events.stream()
				.map(event -> EventMapper.toEventShortDto(
						event,
						confirmedRequests.getOrDefault(event.getId(), 0L),
						viewStats.getOrDefault(event.getId(), 0L)))
				.collect(Collectors.toList());
	}

	@Override
	public EventFullDto getEvent(Long eventId, HttpServletRequest request) {
		Event event = findEvent(eventId);
		if (event.getState() != EventState.PUBLISHED) {
			throw new NotFoundException("Событие не опубликовано");
		}
		eventUtils.saveView(request);

		Map<Long, Long> viewStats = eventUtils.getViews(List.of(event));
		Map<Long, Long> confirmedRequests = eventUtils.getConfirmedRequests(List.of(event));
		return EventMapper.toEventFullDto(
				event,
				confirmedRequests.getOrDefault(event.getId(), 0L),
				viewStats.getOrDefault(event.getId(), 0L)
		);
	}

	@Override
	public List<EventShortDto> publicSearchEvents(
			String text,
			List<Long> categories,
			Boolean paid,
			LocalDateTime rangeStart,
			LocalDateTime rangeEnd,
			Boolean onlyAvailable,
			EventSortType sort,
			Pageable pageable,
			HttpServletRequest request
	) {
		if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
			throw new IllegalArgumentException("Ошибка даты");
		}
		eventUtils.saveView(request);

		Specification<Event> spec = buildPublicSearchSpec(text, categories, paid, rangeStart, rangeEnd, onlyAvailable);
		List<Event> events = eventRepository.findAll(spec, pageable).getContent();

		Map<Long, Long> viewStats = eventUtils.getViews(events);
		switch (sort) {
			case EventSortType.VIEWS:
				events = events
						.stream()
						.sorted(Comparator.comparing(event -> viewStats.getOrDefault(event.getId(), 0L)))
						.collect(Collectors.toList());
			case EventSortType.EVENT_DATE:
				events = events
						.stream()
						.sorted(Comparator.comparing(Event::getEventDate))
						.collect(Collectors.toList());
			default:
				return eventUtils.getListOfEventShortDto(events);
		}
	}

	@Override
	public List<EventFullDto> adminSearchEvents(
			List<Long> users,
			List<EventState> states,
			List<Long> categories,
			LocalDateTime rangeStart,
			LocalDateTime rangeEnd,
			Pageable pageable
	) {
		Specification<Event> spec = buildAdminSearchSpec(users, states, categories, rangeStart, rangeEnd);
		List<Event> events = eventRepository.findAll(spec, pageable).getContent();

		Map<Long, Long> viewStats = eventUtils.getViews(events);
		Map<Long, Long> confirmedRequests = eventUtils.getConfirmedRequests(events);
		return events.stream()
				.map(event -> EventMapper.toEventFullDto(
						event,
						confirmedRequests.getOrDefault(event.getId(), 0L),
						viewStats.getOrDefault(event.getId(), 0L)))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public EventFullDto updateAdminEvent(Long id, UpdateEventAdminRequest updateEventAdminRequest) {
		Event event = findEvent(id);
		checkUpdatedEvent(event, updateEventAdminRequest);

		updateEventFields(event, updateEventAdminRequest);
		updateEventAdminState(event, updateEventAdminRequest.getStateAction());

		Event updatedEvent = eventRepository.save(event);
		return EventMapper.toEventFullDto(updatedEvent, 0L, 0L);
	}

	@Override
	@Transactional
	public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
		User user = findUser(userId);
		Event event = findEvent(eventId);
		checkUpdatedUserEvent(event, user, updateEventUserRequest);

		updateEventFields(event, updateEventUserRequest);
		updateEventUserState(event, updateEventUserRequest.getStateAction());

		Event updatedEvent = eventRepository.save(event);
		Map<Long, Long> confirmedRequests = eventUtils.getConfirmedRequests(List.of(updatedEvent));
		Map<Long, Long> viewStats = eventUtils.getViews(List.of(updatedEvent));
		return EventMapper.toEventFullDto(
				event,
				confirmedRequests.getOrDefault(eventId, 0L),
				viewStats.getOrDefault(eventId, 0L)
		);
	}

	private void updateEventFields(Event event, UpdateEventRequest updateEventRequest) {
		if (updateEventRequest.getLocation() != null) {
			event.setLocation(locationRepository.save(LocationMapper.toLocation(updateEventRequest.getLocation())));
		}
		if (updateEventRequest.getCategoryId() != null) {
			event.setCategory(findCategory(updateEventRequest.getCategoryId()));
		}
		Optional.ofNullable(updateEventRequest.getTitle()).ifPresent(event::setTitle);
		Optional.ofNullable(updateEventRequest.getAnnotation()).ifPresent(event::setAnnotation);
		Optional.ofNullable(updateEventRequest.getDescription()).ifPresent(event::setDescription);
		Optional.ofNullable(updateEventRequest.getPaid()).ifPresent(event::setPaid);
		Optional.ofNullable(updateEventRequest.getRequestModeration()).ifPresent(event::setRequestModeration);
		Optional.ofNullable(updateEventRequest.getParticipantLimit()).ifPresent(event::setParticipantLimit);
		Optional.ofNullable(updateEventRequest.getEventDate()).ifPresent(event::setEventDate);
	}

	private void updateEventUserState(Event event, EventStateAction stateAction) {
		if (stateAction != null) {
			switch (stateAction) {
				case EventStateAction.SEND_TO_REVIEW:
					event.setState(EventState.PENDING);
					break;
				case EventStateAction.CANCEL_REVIEW:
					event.setState(EventState.CANCELED);
					break;
				default:
					throw new ValidationException(String.format("Непредусмотренный тип операции: %s", stateAction));
			}
		}
	}

	private void updateEventAdminState(Event event, EventStateAction stateAction) {
		if (stateAction != null) {
			if (!event.getState().equals(EventState.PENDING)) {
				throw new ConflictException("Событие уже было опубликовано или отклонено");
			}
			switch (stateAction) {
				case EventStateAction.REJECT_EVENT:
					event.setState(EventState.CANCELED);
					break;
				case EventStateAction.PUBLISH_EVENT:
					event.setPublishedOn(LocalDateTime.now());
					event.setState(EventState.PUBLISHED);
					break;
				default:
					throw new ValidationException(String.format("Непредусмотренный тип операции: %s", stateAction));
			}
		}
	}

	private Event findEvent(Long eventId) {
		return eventRepository.findById(eventId)
				.orElseThrow(() -> new NotFoundException(String.format("Событие c id %d не найдено", eventId)));
	}

	private User findUser(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException(String.format("Пользователь c id %d не найден", userId)));
	}

	private Category findCategory(Long categoryId) {
		return categoryRepository.findById(categoryId)
				.orElseThrow(() -> new NotFoundException(String.format("Категория c id %d не найдена", categoryId)));
	}

	private void checkNewEvent(NewEventDto newEventDto) {
		if (newEventDto.getEventDate() != null
				&& newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
			throw new ValidationException("Ошибка даты: до даты оосталось менее часа");
		}
	}

	private void checkUpdatedUserEvent(Event event, User user, UpdateEventUserRequest updateEventUserRequest) {
		if (updateEventUserRequest.getEventDate() != null
				&& updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
			throw new ValidationException("Ошибка даты: до даты оосталось менее часа");
		} else if (!event.getInitiator().equals(user)) {
			throw new ConflictException(String.format("Пользователь c id %d не является создателем события", user.getId()));
		} else if (event.getState().equals(EventState.PUBLISHED)) {
			throw new ConflictException("Событие уже опубликовано");
		}
	}

	private void checkUpdatedEvent(Event event, UpdateEventAdminRequest updateEventAdminRequest) {
		if (updateEventAdminRequest.getEventDate() != null &&
				updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1)) ||
				LocalDateTime.now().isAfter(event.getEventDate())) {
			throw new ValidationException("Ошибка даты: событие уже прошло или до даты оосталось менее часа");
		}
	}

	private Specification<Event> buildPublicSearchSpec(
			String text,
			List<Long> categories,
			Boolean paid,
			LocalDateTime rangeStart,
			LocalDateTime rangeEnd,
			Boolean onlyAvailable
	) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (text != null && !text.isBlank()) {
				Predicate annotationLike = criteriaBuilder.like(
						criteriaBuilder.lower(root.get("annotation")),
						"%" + text.toLowerCase() + "%"
				);
				Predicate descriptionLike = criteriaBuilder.like(
						criteriaBuilder.lower(root.get("description")),
						"%" + text.toLowerCase() + "%"
				);
				predicates.add(criteriaBuilder.or(annotationLike, descriptionLike));
			}

			if (categories != null && !categories.isEmpty()) {
				predicates.add(root.get("category").get("id").in(categories));
			}

			if (paid != null) {
				predicates.add(criteriaBuilder.equal(root.get("paid"), paid));
			}

			if (rangeStart != null) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
			}

			if (rangeEnd != null) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
			}

			if (Boolean.TRUE.equals(onlyAvailable)) {
				predicates.add(
						criteriaBuilder.or(
								criteriaBuilder.equal(root.get("participantLimit"), 0),
								criteriaBuilder.lessThan(
										root.get("confirmedRequests"),
										root.get("participantLimit")
								)
						)
				);
			}
			predicates.add(criteriaBuilder.equal(root.get("state"), EventState.PUBLISHED));

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

	private Specification<Event> buildAdminSearchSpec(
			List<Long> users,
			List<EventState> states,
			List<Long> categories,
			LocalDateTime rangeStart,
			LocalDateTime rangeEnd
	) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (users != null && !users.isEmpty()) {
				predicates.add(root.get("initiator").get("id").in(users));
			}

			if (states != null && !states.isEmpty()) {
				predicates.add(root.get("state").in(states));
			}

			if (categories != null && !categories.isEmpty()) {
				predicates.add(root.get("category").get("id").in(categories));
			}

			if (rangeStart != null) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
			}

			if (rangeEnd != null) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}