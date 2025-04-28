package ru.practicum.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.models.Location;

@Component
public class LocationMapper {
	public static Location toLocation(LocationDto locationDto) {
		if (locationDto == null) {
			return null;
		}
		return Location.builder()
				.lat(locationDto.getLat())
				.lon(locationDto.getLon())
				.build();
	}

	public static LocationDto toLocationDto(Location location) {
		if (location == null) {
			return null;
		}
		return LocationDto.builder()
				.lat(location.getLat())
				.lon(location.getLon())
				.build();
	}
}