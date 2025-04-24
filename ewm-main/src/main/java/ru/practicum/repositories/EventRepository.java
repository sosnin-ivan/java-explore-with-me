package ru.practicum.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.enums.EventState;
import ru.practicum.models.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
	@Query("SELECT e FROM Event e " +
			"WHERE (:users IS NULL OR e.initiator.id IN :users) " +
			"AND (:states IS NULL OR e.state IN :states) " +
			"AND (:categories IS NULL OR e.category.id IN :categories) " +
			"AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart) " +
			"AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd)")
	Page<Event> adminSearchEvents(
			@Param("users") List<Long> users,
			@Param("states") List<EventState> states,
			@Param("categories") List<Long> categories,
			@Param("rangeStart") LocalDateTime rangeStart,
			@Param("rangeEnd") LocalDateTime rangeEnd,
			Pageable pageable
	);

	@Query("SELECT e FROM Event e " +
			"WHERE (:text IS NULL " +
				"OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
				"OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
			"AND (:categories IS NULL OR e.category.id IN :categories) " +
			"AND (:paid IS NULL OR e.paid = :paid) " +
			"AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart) " +
			"AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd) " +
			"AND (e.state = 'PUBLISHED') " +
			"AND (:onlyAvailable = FALSE OR (e.participantLimit = 0 OR e.participantLimit > (SELECT COUNT(r) FROM Request r WHERE r.event = e AND r.status = 'CONFIRMED')))"
	)
	Page<Event> publicSearchEvents(
			@Param("text") String text,
			@Param("categories") List<Long> categories,
			@Param("paid") Boolean paid,
			@Param("rangeStart") LocalDateTime rangeStart,
			@Param("rangeEnd") LocalDateTime rangeEnd,
			@Param("onlyAvailable") Boolean onlyAvailable,
			Pageable pageable
	);

	List<Event> findAllByInitiatorId(Long id, Pageable pageable);
}