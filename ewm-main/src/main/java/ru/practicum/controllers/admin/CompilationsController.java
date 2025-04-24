package ru.practicum.controllers.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.services.interfaces.CompilationService;

@Slf4j
@RequiredArgsConstructor
@RestController("adminCompilationsController")
@RequestMapping("/admin/compilations")
public class CompilationsController {
	private final CompilationService compilationService;

	@PostMapping
	public ResponseEntity<CompilationDto> createCompilation(
			@RequestBody @Valid NewCompilationDto newCompilationDto
	) {
		log.info("Admin/CompilationsController.createCompilation: {}", newCompilationDto);
		CompilationDto result = compilationService.createCompilation(newCompilationDto);
		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}

	@DeleteMapping("/{compilationId}")
	public ResponseEntity<Void> deleteCompilation(
			@PathVariable @Positive Long compilationId
	) {
		log.info("Admin/CompilationsController.deleteCompilation: id: {}", compilationId);
		compilationService.deleteCompilation(compilationId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PatchMapping("/{compilationId}")
	public ResponseEntity<CompilationDto> updateCompilation(
			@PathVariable @Positive Long compilationId,
			@RequestBody @Valid UpdateCompilationRequest updateCompilationRequest
	) {
		log.info("Admin/CompilationsController.updateCompilation: compilationId: {}, {}", compilationId, updateCompilationRequest);
		CompilationDto result = compilationService.updateCompilation(compilationId, updateCompilationRequest);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}