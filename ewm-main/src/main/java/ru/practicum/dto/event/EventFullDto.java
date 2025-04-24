package ru.practicum.dto.event;

import lombok.Builder;
import lombok.Data;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.enums.EventState;

@Data
@Builder
public class EventFullDto {
	private Long id;
	private UserShortDto initiator;
	private CategoryDto category;
	private LocationDto location;
	private EventState state;
	private String title;
	private String annotation;
	private String description;
	private Boolean paid;
	private Boolean requestModeration;
	private Long participantLimit;
	private String createdOn;
	private String publishedOn;
	private String eventDate;
	private Long confirmedRequests;
	private Long views;
}