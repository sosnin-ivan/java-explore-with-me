package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.enums.RequestStatus;
import ru.practicum.models.Event;
import ru.practicum.models.Request;

import java.util.List;
import java.util.Map;

public interface RequestRepository extends JpaRepository<Request, Long> {
	@Query("SELECT COUNT(r.id)" +
			"FROM Request AS r " +
			"WHERE r.status = :status AND r.event.id IN :ids " +
			"GROUP BY r.event.id")
	Map<Long, Long> findByStatus(@Param("ids") List<Long> ids, @Param("status") RequestStatus status);

	List<Request> findAllByEvent(Event event);

	List<Request> findAllByRequesterId(Long userId);

	List<Request> findAllByRequesterIdAndEventId(Long requesterId, Long eventId);

	Long countByEventIdAndStatus(Long eventId, RequestStatus status);
}