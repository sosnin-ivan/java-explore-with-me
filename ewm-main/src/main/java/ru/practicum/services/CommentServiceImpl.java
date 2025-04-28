package ru.practicum.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.enums.CommentState;
import ru.practicum.enums.EventState;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.CommentMapper;
import ru.practicum.models.Comment;
import ru.practicum.models.Event;
import ru.practicum.models.User;
import ru.practicum.repositories.CommentRepository;
import ru.practicum.repositories.EventRepository;
import ru.practicum.repositories.UserRepository;
import ru.practicum.services.interfaces.CommentService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements CommentService {
	CommentRepository commentRepository;
	EventRepository eventRepository;
	UserRepository userRepository;

	@Override
	public List<CommentFullDto> getCommentsByAuthor(Long authorId, Pageable pageable) {
		findAuthor(authorId);
		List<Comment> comments = commentRepository.findAllByAuthorId(authorId, pageable).getContent();
		return comments.stream().map(CommentMapper::toCommentFullDto).toList();
	}

	@Override
	public List<CommentShortDto> getAllEventComments(Long eventId, Pageable pageable) {
		findEvent(eventId);
		List<Comment> comments = commentRepository.findAllByEventIdAndState(eventId, CommentState.PUBLISHED, pageable).getContent();
		return comments.stream().map(CommentMapper::toCommentShortDto).toList();
	}

	@Override
	@Transactional
	public CommentFullDto createComment(NewCommentDto newCommentDto, Long authorId) {
		User author = findAuthor(authorId);
		Event event = findEvent(newCommentDto.getEventId());
		if (event.getState() != EventState.PUBLISHED) {
			throw new ConflictException("Событие еще не опубликовано");
		}
		Comment comment = CommentMapper.toComment(newCommentDto, event, author);
		return CommentMapper.toCommentFullDto(commentRepository.save(comment));
	}

	@Override
	@Transactional
	public CommentFullDto updateComment(UpdateCommentDto updateCommentDto, Long authorId, Long commentId) {
		findAuthor(authorId);
		Comment comment = findComment(commentId);
		checkAuthor(authorId, comment);
		findEvent(comment.getEvent().getId());
		if (updateCommentDto.getText() != null) {
			comment.setText(updateCommentDto.getText());
		}
		return CommentMapper.toCommentFullDto(commentRepository.save(comment));
	}

	@Override
	@Transactional
	public CommentFullDto updateState(Long commentId, CommentState state) {
		Comment comment = findComment(commentId);
		if (state != null) {
			comment.setState(state);
		}
		return CommentMapper.toCommentFullDto(commentRepository.save(comment));
	}

	@Override
	@Transactional
	public void deleteComment(Long authorId, Long commentId) {
		findAuthor(authorId);
		Comment comment = findComment(commentId);
		checkAuthor(authorId, comment);
		commentRepository.deleteById(commentId);
	}

	@Override
	@Transactional
	public void deleteComment(Long commentId) {
		commentRepository.deleteById(findComment(commentId).getId());
	}

	private Event findEvent(Long eventId) {
		return eventRepository.findById(eventId)
				.orElseThrow(() -> new NotFoundException(String.format("Событие c id %d не найдено", eventId)));
	}

	private Comment findComment(Long commentId) {
		return commentRepository.findById(commentId)
				.orElseThrow(() -> new NotFoundException(String.format("Комментарий c id %d не найден", commentId)));
	}

	private User findAuthor(Long authorId) {
		return userRepository.findById(authorId)
				.orElseThrow(() -> new NotFoundException(String.format("Автор c id %d не найден", authorId)));
	}

	private void checkAuthor(Long authorId, Comment comment) {
		if (!comment.getAuthor().getId().equals(authorId)) {
			throw new ConflictException(String.format("Автор %d не является автором комментария %d", authorId, comment.getId()));
		}
	}
}