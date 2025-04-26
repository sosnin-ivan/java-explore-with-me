package ru.practicum.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.models.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
	Page<Event> findAllByInitiatorId(Long id, Pageable pageable);

	Page<Event> findAll(Specification<Event> spec, Pageable pageable);
}