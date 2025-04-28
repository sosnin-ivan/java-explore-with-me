package ru.practicum.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.EventState;
import ru.practicum.enums.RequestStatus;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.RequestMapper;
import ru.practicum.models.Event;
import ru.practicum.models.Request;
import ru.practicum.models.User;
import ru.practicum.repositories.EventRepository;
import ru.practicum.repositories.RequestRepository;
import ru.practicum.repositories.UserRepository;
import ru.practicum.services.interfaces.RequestService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestServiceImpl implements RequestService {
	RequestRepository requestRepository;
	UserRepository userRepository;
	EventRepository eventRepository;

	@Override
	@Transactional
	public ParticipationRequestDto createRequest(Long userId, Long eventId) {
		User requester = findUser(userId);
		Event event = findEvent(eventId);
		validateRequest(requester, event);
		Request request = Request.builder()
				.requester(requester)
				.event(event)
				.status(setRequestStatus(event))
				.build();
		return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
	}

	@Override
	public List<ParticipationRequestDto> getUserRequests(Long userId) {
		findUser(userId);
		List<Request> requests = requestRepository.findAllByRequesterId(userId);
		if (requests.isEmpty()) {
			return List.of();
		}
		return requests.stream().map(RequestMapper::toParticipationRequestDto).toList();
	}

	@Override
	@Transactional
	public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
		findUser(userId);
		Request request = findRequest(requestId);
		request.setStatus(RequestStatus.CANCELED);
		return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
	}

	@Override
	public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
		findUser(userId);
		Event event = findEvent(eventId);
		List<Request> requests = requestRepository.findAllByEvent(event).stream()
				.filter(request -> request.getEvent().getInitiator().getId().equals(userId))
				.toList();
		if (requests.isEmpty()) {
			return new ArrayList<>();
		}
		return requests.stream()
				.map(RequestMapper::toParticipationRequestDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public EventRequestStatusUpdateResult updateRequestStatus(
			Long userId,
			Long eventId,
			EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest
	) {
		findUser(userId);
		Event event = findEvent(eventId);
		List<Request> userRequests = requestRepository.findAllById(eventRequestStatusUpdateRequest.getRequestIds());
		validateUserRequests(userRequests);

		List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
		List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
		if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.REJECTED)) {
			rejectedRequests = mapRequests(userRequests, RequestStatus.REJECTED);
		} else if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
			if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
				confirmedRequests = mapRequests(userRequests, RequestStatus.CONFIRMED);
			} else if (requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED) >= event.getParticipantLimit()) {
				throw new ConflictException("Достигнут лимит по заявкам на участие");
			}

			for (Request request : userRequests) {
				if (requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED) < event.getParticipantLimit()) {
					request.setStatus(RequestStatus.CONFIRMED);
					confirmedRequests.add(RequestMapper.toParticipationRequestDto(request));
				} else {
					request.setStatus(RequestStatus.REJECTED);
					rejectedRequests.add(RequestMapper.toParticipationRequestDto(request));
				}
				requestRepository.save(request);
			}
		}
		return EventRequestStatusUpdateResult.builder()
				.confirmedRequests(confirmedRequests)
				.rejectedRequests(rejectedRequests)
				.build();
	}

	private RequestStatus setRequestStatus(Event event) {
		if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
			return RequestStatus.CONFIRMED;
		} else {
			return RequestStatus.PENDING;
		}
	}

	private List<ParticipationRequestDto> mapRequests(List<Request> userRequests, RequestStatus status) {
		return userRequests.stream().map(request -> {
			request.setStatus(status);
			return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
		}).collect(Collectors.toList());
	}

	private Request findRequest(Long requestId) {
		return requestRepository.findById(requestId).orElseThrow(() ->
				new NotFoundException(String.format("Запрос с id %d не найден", requestId)));
	}

	private Event findEvent(Long eventId) {
		return eventRepository.findById(eventId).orElseThrow(() ->
				new NotFoundException(String.format("Событие с id %d не найдено", eventId)));
	}

	private User findUser(Long userId) {
		return userRepository.findById(userId).orElseThrow(() ->
				new NotFoundException(String.format("Пользователь c id %d не найден", userId)));
	}

	private void validateUserRequests(List<Request> userRequests) {
		if (userRequests.stream().anyMatch(request -> !request.getStatus().equals(RequestStatus.PENDING))) {
			throw new ConflictException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
		}
	}

	private void validateRequest(User requester, Event event) {
		if (requester.getId().equals(event.getInitiator().getId())) {
			throw new ConflictException("Нельзя запросить участие в собственном событии");
		} else if (!event.getState().equals(EventState.PUBLISHED)) {
			throw new ConflictException("Событие ещё не опубликовано");
		} else if (!requestRepository.findAllByRequesterIdAndEventId(requester.getId(), event.getId()).isEmpty()) {
			throw new ConflictException("Запрос на участие в событии уже был отправлен");
		} else if (event.getParticipantLimit() != 0 &&
				requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED) >= event.getParticipantLimit()) {
			throw new ConflictException("Лимит заявок на участие в событии исчерпан");
		}
	}
}