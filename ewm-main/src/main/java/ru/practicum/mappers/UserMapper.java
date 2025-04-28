package ru.practicum.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.models.User;

@Component
public class UserMapper {
	public static User toUser(NewUserRequest newUserRequest) {
		if (newUserRequest == null) {
			return null;
		}
		return User.builder()
				.email(newUserRequest.getEmail())
				.name(newUserRequest.getName())
				.build();
	}

	public static UserDto toUserDto(User user) {
		if (user == null) {
			return null;
		}
		return UserDto.builder()
				.id(user.getId())
				.email(user.getEmail())
				.name(user.getName())
				.build();
	}

	public static UserShortDto toUserShortDto(User user) {
		if (user == null) {
			return null;
		}
		return UserShortDto.builder()
				.email(user.getEmail())
				.name(user.getName())
				.build();
	}
}