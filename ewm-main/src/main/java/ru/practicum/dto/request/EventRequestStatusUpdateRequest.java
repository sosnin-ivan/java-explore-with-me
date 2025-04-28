package ru.practicum.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import ru.practicum.enums.RequestStatus;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
	@NotEmpty
	private List<Long> requestIds;

	private RequestStatus status;
}