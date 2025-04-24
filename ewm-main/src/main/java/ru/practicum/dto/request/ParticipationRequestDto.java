package ru.practicum.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticipationRequestDto {
	private Long id;
	private Long requester;
	private Long event;
	private String status;
	private String created;
}