package ru.practicum.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserShortDto {
	private String email;
	private String name;
}