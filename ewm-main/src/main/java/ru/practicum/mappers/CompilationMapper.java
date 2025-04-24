package ru.practicum.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.models.Compilation;
import ru.practicum.models.Event;

import java.util.List;

@Component
public class CompilationMapper {
	public static Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> events) {
		if (newCompilationDto == null) {
			return null;
		}
		return Compilation.builder()
				.pinned(newCompilationDto.getPinned())
				.title(newCompilationDto.getTitle())
				.events(events)
				.build();
	}

	public static CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> events) {
		if (compilation == null) {
			return null;
		}
		return CompilationDto.builder()
				.pinned(compilation.getPinned())
				.title(compilation.getTitle())
				.events(events)
				.build();
	}
}