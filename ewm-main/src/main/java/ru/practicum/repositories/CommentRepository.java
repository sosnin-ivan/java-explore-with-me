package ru.practicum.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.enums.CommentState;
import ru.practicum.models.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	Page<Comment> findAllByEventIdAndState(Long eventId, CommentState commentState, Pageable pageable);

	Page<Comment> findAllByAuthorId(Long authorId, Pageable pageable);
}