package ru.practicum.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.HitClient;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.enums.RequestStatus;
import ru.practicum.hit.HitRequest;
import ru.practicum.hit.HitResponse;
import ru.practicum.mappers.EventMapper;
import ru.practicum.models.Event;
import ru.practicum.repositories.RequestRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventUtils {
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	static RequestRepository requestRepository;
	static HitClient hitClient;

	public static Map<Long, Long> getViews(List<Event> events) {
		Map<String, Long> eventUrisAndIds = events.stream()
				.collect(Collectors.toMap(
						event -> String.format("/events/%s", event.getId()),
						Event::getId
				));
		LocalDateTime startDate = events.stream()
				.map(Event::getCreatedOn)
				.min(LocalDateTime::compareTo)
				.orElse(null);
		Map<Long, Long> statsMap = new HashMap<>();

		if (startDate != null) {
			List<HitResponse> stats = hitClient.getStats(
					LocalDateTime.now().minusYears(1).format(DATE_TIME_FORMATTER),
					LocalDateTime.now().format(DATE_TIME_FORMATTER),
					List.copyOf(eventUrisAndIds.keySet()),
					true
			);
			statsMap = stats.stream().collect(Collectors.toMap(
					statsDto -> parseEventIdFromUrl(statsDto.getUri()),
					HitResponse::getHits
			));
		}
		return statsMap;
	}

	public static void saveView(HttpServletRequest request) {
		hitClient.addHit(HitRequest.builder()
				.app("${app}")
				.uri(request.getRequestURI())
				.ip(request.getRemoteAddr())
				.timestamp(LocalDateTime.now())
				.build());
	}

	public static List<EventShortDto> getListOfEventShortDto(List<Event> events) {
		Map<Long, Long> viewStats = EventUtils.getViews(events);
		Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
		return events.stream()
				.map(event -> EventMapper.toEventShortDto(
						event,
						confirmedRequests.getOrDefault(event.getId(), 0L),
						viewStats.getOrDefault(event.getId(), 0L)))
				.collect(Collectors.toList());
	}

	public static Map<Long, Long> getConfirmedRequests(List<Event> events) {
		if (events.isEmpty()) return Collections.emptyMap();
		List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
		return requestRepository.findByStatus(ids, RequestStatus.CONFIRMED);
	}

	private static Long parseEventIdFromUrl(String url) {
		String[] parts = url.split("/events/");
		if (parts.length == 2) {
			return Long.parseLong(parts[1]);
		}
		return -1L;
	}
}