package ru.practicum;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.hit.HitRequest;
import ru.practicum.hit.HitResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class HitClient extends BaseClient {
	public HitClient(RestTemplateBuilder builder) {
		super(builder
				.uriTemplateHandler(new DefaultUriBuilderFactory("http://ewm-stats-server:9090"))
				.requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
				.build());
	}

	public void addHit(HttpServletRequest request) {
		post(HitRequest.builder()
				.app("ewm-main-service")
				.uri(request.getRequestURI())
				.ip(request.getRemoteAddr())
				.timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
				.build());
	}

	public List<HitResponse> getStats(String start, String end, List<String> uris, boolean unique) {
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
				.fromPath("/stats")
				.queryParam("start", start)
				.queryParam("end", end)
				.queryParam("uris", uris)
				.queryParam("unique", unique);
		try {
			ResponseEntity<List<HitResponse>> response = rest.exchange(
					uriComponentsBuilder.build().toString(),
					HttpMethod.GET,
					null,
					new ParameterizedTypeReference<>() {}
			);
			if (response.getStatusCode().is2xxSuccessful()) {
				return response.getBody();
			} else {
				return List.of();
			}
		} catch (Exception e) {
			return List.of();
		}
	}
}