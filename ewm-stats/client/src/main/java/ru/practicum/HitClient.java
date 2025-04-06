package ru.practicum;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.hit.HitRequest;

import java.util.List;

public class HitClient extends BaseClient {
	// TODO: move to application.yaml
	private static final String SERVER_URL = "http://localhost:8080";

	public HitClient(RestTemplateBuilder builder) {
		super(builder
				.uriTemplateHandler(new DefaultUriBuilderFactory(SERVER_URL))
				.requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
				.build());
	}

	public void addHit(HitRequest hitRequest) {
		post(hitRequest);
	}

	public ResponseEntity<Object> getStats(String start, String end, List<String> uris, boolean unique) {
		return get(UriComponentsBuilder
				.fromPath("/stats")
				.queryParam("start", start)
				.queryParam("end", end)
				.queryParam("uris", uris)
				.queryParam("unique", unique)
				.toUriString()
		);
	}
}