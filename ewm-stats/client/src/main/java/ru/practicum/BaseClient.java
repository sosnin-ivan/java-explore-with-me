package ru.practicum;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class BaseClient {
	protected final RestTemplate rest;

	public BaseClient(RestTemplate rest) {
		this.rest = rest;
	}

	protected ResponseEntity<Object> get(String path) {
		return makeAndSendRequest(HttpMethod.GET, path, null);
	}

	protected <T> void post(T body) {
		makeAndSendRequest(HttpMethod.POST, "/hit", body);
	}

	private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, @Nullable T body) {
		HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

		ResponseEntity<Object> serverResponse;
		try {
			serverResponse = rest.exchange(path, method, requestEntity, Object.class);
		} catch (HttpStatusCodeException e) {
			return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
		}
		return prepareGatewayResponse(serverResponse);
	}

	private HttpHeaders defaultHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));
		return headers;
	}

	private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
		if (response.getStatusCode().is2xxSuccessful()) {
			return response;
		}

		ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

		if (response.hasBody()) {
			return responseBuilder.body(response.getBody());
		}

		return responseBuilder.build();
	}
}