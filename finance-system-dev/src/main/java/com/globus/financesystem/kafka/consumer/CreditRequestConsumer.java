package com.globus.financesystem.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globus.financesystem.kafka.dto.TransferRequest;
import com.globus.financesystem.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreditRequestConsumer {

    private final TransferService transferService;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @KafkaListener(topics = "${spring.kafka.topic.payment-account}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTransferRequest(String message) {
        TransferRequest dto = objectMapper.readValue(message, TransferRequest.class);
        log.info("[KAFKA Получено сообщение TransferRequest с transactionId={}", dto.transactionId());

        transferService.processTransferRequest(dto);
        log.info("[KAFKA] Обработка сообщения c transactionId={} завершена", dto.transactionId());

    }
}
