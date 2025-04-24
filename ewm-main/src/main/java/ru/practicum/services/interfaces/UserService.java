package ru.practicum.services.interfaces;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {
	UserDto createUser(NewUserRequest newUserRequest);

	void deleteUser(Long id);

	List<UserDto> getUsers(List<Long> ids, Pageable pageable);
}