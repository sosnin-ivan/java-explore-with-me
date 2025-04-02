package ru.practicum.hit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {
	@Query("""
		SELECT new ru.practicum.hit.HitResponse(h.app, h.uri, COUNT(h.ip))
		FROM Hit h
		WHERE h.timestamp BETWEEN :start AND :end
		AND h.uri IN :uris
		GROUP BY h.app, h.uri
		ORDER BY COUNT(h.ip) DESC
	""")
	List<HitResponse> findNotUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

	@Query("""
		SELECT new ru.practicum.hit.HitResponse(h.app, h.uri, COUNT(DISTINCT h.ip))
		FROM Hit h
		WHERE h.timestamp BETWEEN :start AND :end
		AND h.uri IN :uris
		GROUP BY h.app, h.uri
		ORDER BY COUNT(DISTINCT h.ip) DESC
	""")
	List<HitResponse> findUnique(LocalDateTime start, LocalDateTime end, List<String> uris);
}