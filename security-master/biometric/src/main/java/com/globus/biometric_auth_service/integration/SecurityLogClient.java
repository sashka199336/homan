package com.globus.biometric_auth_service.integration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.globus.biometric_auth_service.dto.SecurityLog;

@Component
public class SecurityLogClient {

    private static final String SECURITY_LOGS_URL = "http://localhost:8080/api/biometric";
    private static final String INTERNAL_SECRET = "biometric-internal-secret-42";

    private final WebClient webClient;

    // 👇 Добавь ЯВНЫЙ конструктор с Qualifier!
    public SecurityLogClient(
            @Qualifier("modul26ServiceWebClient") WebClient webClient // <-- или sessionTracingServiceWebClient если надо
    ) {
        this.webClient = webClient;
    }

    public void sendSecurityEvent(SecurityLog log) {
        webClient.post()
                .uri(SECURITY_LOGS_URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("X-Internal-Secret", INTERNAL_SECRET)
                .bodyValue(log)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(resp -> System.out.println("SecurityLog отправлен: " + log + ", status: " + resp.getStatusCode()))
                .doOnError(err -> System.err.println("Ошибка отправки security event: " + err.getMessage()))
                .onErrorResume(err -> reactor.core.publisher.Mono.empty())
                .block();
    }
}