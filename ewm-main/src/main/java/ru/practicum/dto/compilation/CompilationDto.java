package ru.practicum.dto.compilation;

import lombok.Builder;
import lombok.Data;
import ru.practicum.dto.event.EventShortDto;

import java.util.List;

@Data
@Builder
public class CompilationDto {
	private Long id;
	private Boolean pinned;
	private String title;
	private List<EventShortDto> events;
}