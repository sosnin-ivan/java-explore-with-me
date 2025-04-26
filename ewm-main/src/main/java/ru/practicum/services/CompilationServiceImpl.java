package ru.practicum.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.CompilationMapper;
import ru.practicum.mappers.EventMapper;
import ru.practicum.models.Compilation;
import ru.practicum.models.Event;
import ru.practicum.repositories.CompilationRepository;
import ru.practicum.repositories.EventRepository;
import ru.practicum.services.interfaces.CompilationService;
import ru.practicum.utils.EventUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompilationServiceImpl implements CompilationService {
	CompilationRepository compilationRepository;
	EventRepository eventRepository;
	EventUtils eventUtils;

	@Override
	@Transactional
	public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
		List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
		Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, events);
		List<EventShortDto> eventsShortDto = eventUtils.getListOfEventShortDto(events);
		return CompilationMapper.toCompilationDto(compilationRepository.save(compilation), eventsShortDto);
	}

	@Override
	public List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable) {
		List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageable).getContent();
		List<CompilationDto> listOfCompilationDto = new ArrayList<>();
		for (Compilation compilation : compilations) {
			Map<Long, Long> viewStats = eventUtils.getViews(compilation.getEvents());
			Map<Long, Long> confirmedRequests = eventUtils.getConfirmedRequests(compilation.getEvents());
			List<EventShortDto> eventsShortDto = compilation.getEvents().stream()
					.map(event -> EventMapper.toEventShortDto(
							event,
							confirmedRequests.getOrDefault(event.getId(), 0L),
							viewStats.getOrDefault(event.getId(), 0L)))
					.toList();
			listOfCompilationDto.add(CompilationMapper.toCompilationDto(compilation, eventsShortDto));
		}
		return listOfCompilationDto;
	}

	@Override
	public CompilationDto getCompilation(Long compilationId) {
		Compilation compilation = findCompilation(compilationId);
		Map<Long, Long> viewStats = eventUtils.getViews(compilation.getEvents());
		Map<Long, Long> confirmedRequests = eventUtils.getConfirmedRequests(compilation.getEvents());
		List<EventShortDto> listOfEventShortDto = compilation.getEvents().stream()
				.map(event -> EventMapper.toEventShortDto(event,
						viewStats.getOrDefault(event.getId(), 0L),
						confirmedRequests.getOrDefault(event.getId(), 0L)))
				.collect(Collectors.toList());
		return CompilationMapper.toCompilationDto(compilation, listOfEventShortDto);
	}

	@Override
	@Transactional
	public CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequest updateCompilationRequest) {
		Compilation compilation = findCompilation(compilationId);
		List<Event> events = eventRepository.findAllById(updateCompilationRequest.getEvents());
		compilation.setEvents(events);
		if (updateCompilationRequest.getPinned() != null) {
			compilation.setPinned(updateCompilationRequest.getPinned());
		}
		if (updateCompilationRequest.getTitle() != null) {
			compilation.setTitle(updateCompilationRequest.getTitle());
		}
		List<EventShortDto> eventsShortDto = eventUtils.getListOfEventShortDto(events);
		return CompilationMapper.toCompilationDto(compilationRepository.save(compilation), eventsShortDto);
	}

	@Override
	@Transactional
	public void deleteCompilation(Long compilationId) {
		findCompilation(compilationId);
		compilationRepository.deleteById(compilationId);
	}

	private Compilation findCompilation(Long id) {
		return compilationRepository.findById(id).orElseThrow(() ->
				new NotFoundException(String.format("Подборка c id %d не найдена", id)));
	}
}