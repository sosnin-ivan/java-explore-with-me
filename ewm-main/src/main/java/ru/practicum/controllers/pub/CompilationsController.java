package ru.practicum.controllers.pub;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.services.CompilationServiceImpl;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController("publicCompilationsController")
@RequestMapping("/compilations")
public class CompilationsController {
	private final CompilationServiceImpl compilationService;

	@GetMapping
	public ResponseEntity<List<CompilationDto>> getCompilations(
			@RequestParam(defaultValue = "false") Boolean pinned,
			@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
			@RequestParam(defaultValue = "10") @Positive Integer size
	) {
		log.info("Pub/CompilationsController.getCompilations: pinned: {}, from: {}, size: {}", pinned, from, size);
		List<CompilationDto> compilations = compilationService.getCompilations(pinned, PageRequest.of(from / size, size));
		return new ResponseEntity<>(compilations, HttpStatus.OK);
	}

	@GetMapping("/{compId}")
	public ResponseEntity<CompilationDto> getCompilation(
			@PathVariable @Positive Long compId
	) {
		log.info("Pub/CompilationsController.getCompilation: compId: {}", compId);
		CompilationDto compilationDto = compilationService.getCompilation(compId);
		return new ResponseEntity<>(compilationDto, HttpStatus.OK);
	}
}