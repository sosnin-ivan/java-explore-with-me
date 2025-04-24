package ru.practicum;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class BaseClient {
	protected final RestTemplate rest;

	public BaseClient(RestTemplate rest) {
		this.rest = rest;
	}

	protected <T> void post(T body) {
		makeAndSendRequest(body);
	}

	private <T> void makeAndSendRequest(@Nullable T body) {
		HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

		ResponseEntity<Object> serverResponse;
		try {
			serverResponse = rest.exchange("/hit", HttpMethod.POST, requestEntity, Object.class);
		} catch (HttpStatusCodeException e) {
			ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
			return;
		}
		prepareGatewayResponse(serverResponse);
	}

	private HttpHeaders defaultHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));
		return headers;
	}

	private static void prepareGatewayResponse(ResponseEntity<Object> response) {
		if (response.getStatusCode().is2xxSuccessful()) {
			return;
		}

		ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

		if (response.hasBody()) {
			responseBuilder.body(response.getBody());
			return;
		}

		responseBuilder.build();
	}
}