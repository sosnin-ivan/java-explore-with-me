package ru.practicum.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.models.Request;
import ru.practicum.utils.Constants;

import java.time.format.DateTimeFormatter;

@Component
public class RequestMapper {
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT);

	public static ParticipationRequestDto toParticipationRequestDto(Request request) {
		if (request == null) {
			return null;
		}
		return ParticipationRequestDto.builder()
				.id(request.getId())
				.created(request.getCreated().format(DATE_TIME_FORMATTER))
				.event(request.getEvent().getId())
				.requester(request.getRequester().getId())
				.status(request.getStatus().toString())
				.build();
	}
}