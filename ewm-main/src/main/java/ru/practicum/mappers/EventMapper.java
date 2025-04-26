package ru.practicum.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.models.Category;
import ru.practicum.models.Event;
import ru.practicum.models.Location;
import ru.practicum.models.User;
import ru.practicum.utils.Constants;

import java.time.format.DateTimeFormatter;

@Component
public class EventMapper {
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT);

	public static Event toEvent(NewEventDto newEventDto, User initiator, Category category, Location location) {
		if (newEventDto == null) {
			return null;
		}
		return Event.builder()
				.initiator(initiator)
				.category(category)
				.location(location)
				.title(newEventDto.getTitle())
				.annotation(newEventDto.getAnnotation())
				.description(newEventDto.getDescription())
				.paid(newEventDto.getPaid())
				.requestModeration(newEventDto.getRequestModeration())
				.participantLimit(newEventDto.getParticipantLimit())
				.eventDate(newEventDto.getEventDate())
				.build();
	}

	public static EventFullDto toEventFullDto(Event event, Long confirmedRequests, Long views) {
		String publishedOn = null;
		if (event.getPublishedOn() != null) {
			publishedOn = event.getPublishedOn().format(DATE_TIME_FORMATTER);
		}
		return EventFullDto.builder()
				.id(event.getId())
				.initiator(UserMapper.toUserShortDto(event.getInitiator()))
				.category(CategoryMapper.toCategoryDto(event.getCategory()))
				.location(LocationMapper.toLocationDto(event.getLocation()))
				.state(event.getState())
				.title(event.getTitle())
				.annotation(event.getAnnotation())
				.description(event.getDescription())
				.paid(event.getPaid())
				.requestModeration(event.getRequestModeration())
				.participantLimit(event.getParticipantLimit())
				.createdOn(event.getCreatedOn().format(DATE_TIME_FORMATTER))
				.publishedOn(publishedOn)
				.eventDate(event.getEventDate().format(DATE_TIME_FORMATTER))
				.confirmedRequests(confirmedRequests)
				.views(views)
				.build();
	}

	public static EventShortDto toEventShortDto(Event event, Long confirmedRequests, Long views) {
		if (event == null) {
			return null;
		}
		return EventShortDto.builder()
				.id(event.getId())
				.initiator(UserMapper.toUserShortDto(event.getInitiator()))
				.category(CategoryMapper.toCategoryDto(event.getCategory()))
				.title(event.getTitle())
				.annotation(event.getAnnotation())
				.paid(event.getPaid())
				.eventDate(event.getEventDate().format(DATE_TIME_FORMATTER))
				.confirmedRequests(confirmedRequests)
				.views(views)
				.build();
	}
}