package ru.practicum.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewCommentDto {
	@NotNull
	private Long eventId;

	@NotBlank
	@Size(min = 1, max = 1000)
	private String text;
}