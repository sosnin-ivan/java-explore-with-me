package ru.practicum.dto.event;

import ru.practicum.dto.location.LocationDto;
import ru.practicum.enums.EventStateAction;

import java.time.LocalDateTime;

public interface UpdateEventRequest {
	Long getCategoryId();
	LocationDto getLocation();
	EventStateAction getStateAction();
	String getTitle();
	String getAnnotation();
	String getDescription();
	Boolean getPaid();
	Boolean getRequestModeration();
	Long getParticipantLimit();
	LocalDateTime getEventDate();
}