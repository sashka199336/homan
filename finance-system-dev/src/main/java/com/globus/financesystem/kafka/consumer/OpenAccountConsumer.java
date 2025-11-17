package com.globus.financesystem.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globus.financesystem.kafka.dto.OpenAccountDto;
import com.globus.financesystem.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenAccountConsumer {

    private final AccountService accountService;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @KafkaListener(topics = "${spring.kafka.topic.claim-account}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenOpenCloseAccount(String message) {
        OpenAccountDto dto = objectMapper.readValue(message, OpenAccountDto.class);

        log.info("[KAFKA] Получено новое сообщение от Claim с customerId: {}", dto.customerId());

        accountService.createAccount(dto);

        log.info("[KAFKA] Обработана заявка от Claim с customerId: {}", dto.customerId());
    }
}
