package ru.practicum.hit;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class HitController {
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private final HitService service;

	@PostMapping("/hit")
	@ResponseStatus(HttpStatus.CREATED)
	public void saveHit(
			@Valid @RequestBody HitRequest hitRequest
	) {
		log.info("HitController: saveHit: {}", hitRequest);
		service.saveHit(hitRequest);
	}

	@GetMapping("/stats")
	public List<HitResponse> getStats(
			@RequestParam(name = "start") @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime start,
			@RequestParam(name = "end") @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime end,
			@RequestParam(name = "uris", required = false) List<String> uris,
			@RequestParam(name = "unique", defaultValue = "false") boolean unique
	) {
		log.info("HitController: getStats: start = {}, end = {}, uris = {}, unique = {}", start, end, uris, unique);
		return service.getStats(start, end, uris, unique);
	}
}