package ru.practicum.dto.event;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.practicum.dto.location.LocationDto;

import java.time.LocalDateTime;

@Data
public class NewEventDto {
	@NotNull
	private Long category;

	@NotNull
	private LocationDto location;

	@NotBlank
	@Size(min = 3, max = 120)
	private String title;

	@NotBlank
	@Size(min = 20, max = 2000)
	private String annotation;

	@NotBlank
	@Size(min = 20, max = 7000)
	private String description;

	private Boolean paid = false;

	private Boolean requestModeration = true;

	@PositiveOrZero
	private Long participantLimit = 0L;

	@Future
	@NotNull
	private LocalDateTime eventDate;
}