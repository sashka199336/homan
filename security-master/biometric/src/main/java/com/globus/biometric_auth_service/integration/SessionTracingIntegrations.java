package com.globus.biometric_auth_service.integration;

import com.globus.biometric_auth_service.dto.SessionDto;
import com.globus.biometric_auth_service.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
public class SessionTracingIntegrations {

    private final WebClient webClient;

    // 👇 Правильный инжект WebClient с явным Qualifier!
    public SessionTracingIntegrations(
            @Qualifier("sessionTracingServiceWebClient") WebClient webClient
    ) {
        this.webClient = webClient;
    }

    public SessionDto login(Integer userId, String deviceInfo, String method, String ipAddress) {
        SessionDto sessionDto = SessionDto.builder()
                .userId(userId)
                .deviceInfo(deviceInfo)
                .method(method)
                .ipAddress(ipAddress)
                .build();

        return webClient.post()
                .uri("/api/v1/sessions")
                .bodyValue(sessionDto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleResponse)
                .bodyToMono(SessionDto.class)
                .block();
    }

    public void prolongSession(Integer userId, String deviceInfo) {
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/prolong")
                        .queryParam("user_id", userId)
                        .queryParam("device_info", deviceInfo)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleResponse)
                .toBodilessEntity()
                .block();
    }

    public void logout(Integer userId, String deviceInfo) {
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/logout")
                        .queryParam("user_id", userId)
                        .queryParam("device_info", deviceInfo)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleResponse)
                .toBodilessEntity()
                .block();
    }

    private Mono<? extends Throwable> handleResponse(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(Map.class)
                .defaultIfEmpty(Map.of())
                .flatMap(body -> {
                    HttpStatusCode code = clientResponse.statusCode();
                    HttpStatus status = HttpStatus.resolve(code.value());
                    String message = body != null && body.get("message") != null
                            ? body.get("message").toString()
                            : "Unknown error from session tracing service";

                    if (status == HttpStatus.NOT_FOUND) {
                        log.info("NOT_FOUND: {}", message);
                        return Mono.error(new ResourceNotFoundException(message));
                    }
                    if (status == HttpStatus.BAD_REQUEST) {
                        log.info("BAD_REQUEST: {}", message);
                        return Mono.error(new IllegalArgumentException(message));
                    }
                    if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
                        log.info("INTERNAL_SERVER_ERROR: {}", message);
                        return Mono.error(new IllegalStateException(message));
                    }
                    log.info("UNHANDLED STATUS {}: {}", code.value(), message);
                    return Mono.error(new RuntimeException("Error: " + code.value() + ", " + message));
                });
    }
}