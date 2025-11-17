package com.globus.damiaadapter.service;

import com.globus.damiaadapter.dto.external.CompanyFinancialScoringResponse;
import com.globus.damiaadapter.dto.external.CompanyRiskScoringResponse;
import com.globus.damiaadapter.dto.internal.CompanyRiskRequestEvent;
import com.globus.damiaadapter.dto.internal.CompanyRiskResponseEvent;
import com.globus.damiaadapter.dto.internal.CompanyScoringRequestEvent;
import com.globus.damiaadapter.dto.internal.CompanyScoringResponseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class KafkaMessageConsumer {
    private final DamiaService damiaService;
    private final KafkaMessageProducer kafkaMessageProducer;

    @KafkaListener(topics = "${app.kafka.topics.scoring-request}")
    public void handlePassportCheck(@Payload CompanyScoringRequestEvent message) {
        log.info("Received inn scoring request: ReqiestID: {}", message.getRequestID());
        Optional<CompanyFinancialScoringResponse> response = damiaService
                .getCompanyCoefficients(message.getInn());

        CompanyScoringResponseEvent responseEvent = response
                .map(data -> new CompanyScoringResponseEvent(
                        message.getRequestID(),
                        CompanyScoringResponseEvent.ResponseStatus.SUCCESS,
                        data))
                .orElseGet(() -> new CompanyScoringResponseEvent(
                        message.getRequestID(),
                        CompanyScoringResponseEvent.ResponseStatus.NOT_FOUND,
                        null
                ));

        kafkaMessageProducer.sendScoringResult(responseEvent);
    }

    @KafkaListener(topics = "${app.kafka.topics.risk-request}")
    public void handleRiskCheck(@Payload CompanyRiskRequestEvent message) {
        log.info("Received risk scoring request: RequestID: {}, Model: {}",
                message.getRequestID(), message.getModel());

        Optional<CompanyRiskScoringResponse> response = damiaService
                .getCompanyRiskScoring(message.getInn(), message.getModel());

        CompanyRiskResponseEvent responseEvent = response
                .map(data -> new CompanyRiskResponseEvent(
                        message.getRequestID(),
                        CompanyRiskResponseEvent.ResponseStatus.SUCCESS,
                        data))
                .orElseGet(() -> new CompanyRiskResponseEvent(
                        message.getRequestID(),
                        CompanyRiskResponseEvent.ResponseStatus.NOT_FOUND,
                        null
                ));

        kafkaMessageProducer.sendRiskResult(responseEvent);
    }
}
