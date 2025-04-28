package ru.practicum.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.models.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
	@EntityGraph(attributePaths = {"category"})
	Page<Event> findAllByInitiatorId(Long id, Pageable pageable);

	@EntityGraph(attributePaths = {"initiator", "category", "location"})
	Page<Event> findAll(Specification<Event> spec, Pageable pageable);
}