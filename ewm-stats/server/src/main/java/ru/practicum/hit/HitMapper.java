package ru.practicum.hit;

import org.springframework.stereotype.Component;

@Component
public class HitMapper {
	public static Hit toHit(HitRequest request) {
		return Hit.builder()
				.app(request.getApp())
				.uri(request.getUri())
				.ip(request.getIp())
				.timestamp(request.getTimestamp())
				.build();
	}
}