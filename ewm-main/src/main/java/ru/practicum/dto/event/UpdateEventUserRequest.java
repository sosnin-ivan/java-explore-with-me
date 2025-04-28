package ru.practicum.dto.event;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.enums.EventStateAction;

import java.time.LocalDateTime;

@Data
public class UpdateEventUserRequest implements UpdateEventRequest {
	private Long categoryId;

	private LocationDto location;

	private EventStateAction stateAction;

	@Size(min = 3, max = 120)
	private String title;

	@Size(min = 20, max = 2000)
	private String annotation;

	@Size(min = 20, max = 7000)
	private String description;

	private Boolean paid;

	private Boolean requestModeration;

	@PositiveOrZero
	private Long participantLimit;

	@Future
	private LocalDateTime eventDate;
}