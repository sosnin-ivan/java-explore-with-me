package ru.practicum.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
	private Long id;
	private String email;
	private String name;
}