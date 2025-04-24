package ru.practicum;

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

import java.util.List;

@Service
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