package ru.practicum.services.interfaces;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
	CompilationDto createCompilation(NewCompilationDto newCompilationDto);

	List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable);

	CompilationDto getCompilation(Long compilationId);

	CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequest compilationDto);

	void deleteCompilation(Long compilationId);
}