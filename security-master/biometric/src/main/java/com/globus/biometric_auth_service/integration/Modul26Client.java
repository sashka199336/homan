package com.globus.biometric_auth_service.integration;

import com.globus.biometric_auth_service.dto.SecurityLogDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class Modul26Client {

    private final WebClient webClient;

    // 👇 Правильный инжект WebClient с явным Qualifier!
    public Modul26Client(
            @Qualifier("modul26ServiceWebClient") WebClient webClient
    ) {
        this.webClient = webClient;
    }

    public void notifyBlockedUser(Long userId, boolean suspicion) {
        notifyBlockedUser(userId, suspicion, "biometric_auth_service: user is blocked due to too many failed biometric attempts");
    }

    public void notifyBlockedUser(Long userId, boolean suspicion, String reason) {
        SecurityLogDTO dto = new SecurityLogDTO(userId, suspicion, reason);

        webClient.post()
                .uri("/api/logs/suspect")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("X-Internal-Secret", "biometric-internal-secret-42")
                .bodyValue(dto)
                .retrieve()
                .toBodilessEntity()
                .doOnError(e -> System.err.println("Ошибка отправки подозрительного события в modul26: " + e.getMessage()))
                .onErrorResume(e -> reactor.core.publisher.Mono.empty())
                .block();
    }
}