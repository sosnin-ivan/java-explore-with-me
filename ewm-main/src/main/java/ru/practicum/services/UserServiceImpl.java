package ru.practicum.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.UserMapper;
import ru.practicum.models.User;
import ru.practicum.repositories.UserRepository;
import ru.practicum.services.interfaces.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
	UserRepository userRepository;

	@Override
	@Transactional
	public UserDto createUser(NewUserRequest newUserRequest) {
		User createdUser = userRepository.save(UserMapper.toUser(newUserRequest));
		return UserMapper.toUserDto(createdUser);
	}

	@Override
	@Transactional
	public void deleteUser(Long id) {
		findUser(id);
		userRepository.deleteById(id);
	}

	@Override
	public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
		if (ids != null && !ids.isEmpty()) {
			return userRepository.findAllByIdIn(ids, pageable).getContent().stream()
					.map(UserMapper::toUserDto)
					.collect(Collectors.toList());
		} else {
			return userRepository.findAll().stream()
					.map(UserMapper::toUserDto)
					.collect(Collectors.toList());
		}
	}

	private void findUser(Long id) {
		userRepository.findById(id).orElseThrow(() ->
				new NotFoundException(String.format("Пользователь c id %d не найден", id)));
	}
}