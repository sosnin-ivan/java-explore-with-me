package ru.practicum.dto.event;

import lombok.Builder;
import lombok.Data;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;

@Data
@Builder
public class EventShortDto {
	private Long id;
	private UserShortDto initiator;
	private CategoryDto category;
	private String title;
	private String annotation;
	private Boolean paid;
	private String eventDate;
	private Long confirmedRequests;
	private Long views;
}