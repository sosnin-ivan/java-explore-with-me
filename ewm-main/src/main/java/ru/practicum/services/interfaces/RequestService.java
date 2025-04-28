package ru.practicum.services.interfaces;

import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
	ParticipationRequestDto createRequest(Long userId, Long eventId);

	List<ParticipationRequestDto> getUserRequests(Long userId);

	ParticipationRequestDto cancelRequest(Long userId, Long requestId);

	List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

	EventRequestStatusUpdateResult updateRequestStatus(
			Long userId,
			Long eventId,
			EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest
	);
}