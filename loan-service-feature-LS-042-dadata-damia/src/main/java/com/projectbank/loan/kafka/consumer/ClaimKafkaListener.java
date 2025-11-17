package com.projectbank.loan.kafka.consumer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.projectbank.loan.dto.ClaimDocumentPackageDto;
import com.projectbank.loan.dto.ClientFinalDecisionDto;
import com.projectbank.loan.service.LoanApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClaimKafkaListener {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final LoanApplicationService loanApplicationService;

    @SneakyThrows
    @KafkaListener(topics = "${spring.kafka.topic.claim-loan.created}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenClaimDocumentPackage(String message) {
        ClaimDocumentPackageDto dto = objectMapper.readValue(message, ClaimDocumentPackageDto.class);
        log.info("[KAFKA] Получено новое сообщение ClaimDocumentPackageDto с claimId: {}", dto.claimId());

        //TODO: прописать логику обработки dto

        log.info("[KAFKA] Обработана заявка по кредиту с claimId: {}", dto.claimId());
    }

    @SneakyThrows
    @KafkaListener(topics = "${spring.kafka.topic.claim-loan.confirmed}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenClaimClientFinalDecision(String message) {
        ClientFinalDecisionDto dto = objectMapper.readValue(message, ClientFinalDecisionDto.class);
        log.info("[KAFKA] Получено новое сообщение ClientFinalDecisionDto с claimId: {}", dto.claimId());

        //TODO: прописать логику обработки dto

        log.info("[KAFKA] Обработано сообщение c решением клиента с claimId: {}", dto.claimId());
    }
}
