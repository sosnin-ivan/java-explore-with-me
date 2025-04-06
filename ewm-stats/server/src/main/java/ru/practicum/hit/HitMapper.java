package ru.practicum.hit;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class HitMapper {
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static Hit toHit(HitRequest request) {
		return Hit.builder()
				.app(request.getApp())
				.uri(request.getUri())
				.ip(request.getIp())
				.timestamp(
						LocalDateTime.parse(
								request.getTimestamp(),
								DateTimeFormatter.ofPattern(DATE_FORMAT)
						)
				)
				.build();
	}
}