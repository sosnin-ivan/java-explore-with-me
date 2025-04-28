package ru.practicum.hit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HitService {
	private final HitRepository hitRepository;

	@Transactional
	public void saveHit(HitRequest hitRequest) {
		hitRepository.save(HitMapper.toHit(hitRequest));
	}

	public List<HitResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
		if (start.isAfter(end)) {
			throw new IllegalArgumentException("Начало периода не может быть позже конца периода");
		}
		if (unique) {
			return hitRepository.findUnique(start, end, uris);
		} else {
			return hitRepository.findNotUnique(start, end, uris);
		}
	}
}