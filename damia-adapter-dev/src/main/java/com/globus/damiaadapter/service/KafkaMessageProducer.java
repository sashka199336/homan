package com.globus.damiaadapter.service;

import com.globus.damiaadapter.dto.internal.CompanyRiskResponseEvent;
import com.globus.damiaadapter.dto.internal.CompanyScoringResponseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Slf4j
@Service
public class KafkaMessageProducer {
    private final KafkaTemplate<String, CompanyScoringResponseEvent> sendScoringCheckTemplate;
    private final String scoringResponseTopic;
    private final KafkaTemplate<String, CompanyRiskResponseEvent> sendRiskCheckTemplate;
    private final String riskResponseTopic;

    public KafkaMessageProducer(
            KafkaTemplate<String, CompanyScoringResponseEvent> sendScoringCheck,
            KafkaTemplate<String, CompanyRiskResponseEvent> sendRiskCheck,
            @Value("${app.kafka.topics.scoring-response}") String scoringResponseTopic,
            @Value("${app.kafka.topics.risk-response}") String riskResponseTopic) {
        this.sendScoringCheckTemplate = sendScoringCheck;
        this.sendRiskCheckTemplate = sendRiskCheck;
        this.scoringResponseTopic = scoringResponseTopic;
        this.riskResponseTopic = riskResponseTopic;
    }

    public void sendScoringResult(CompanyScoringResponseEvent message) {
        UUID requestId = message.getRequestID();
        log.info("Sending scoring result (RequestID: {})", requestId);
        sendScoringCheckTemplate.send(scoringResponseTopic, message)
                .whenComplete((res, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send scoring result (RequestID: {})", requestId, ex);
                    } else {
                        log.info("Successfully sent scoring result (RequestID: {}), Offset: {}",
                                requestId, res.getRecordMetadata().offset());
                    }
                });
    }

    public void sendRiskResult(CompanyRiskResponseEvent message) {
        UUID requestId = message.getRequestID();
        log.info("Sending risk result (RequestID: {})", requestId);
        sendRiskCheckTemplate.send(riskResponseTopic, message)
                .whenComplete((res, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send risk result (RequestID: {})", requestId, ex);
                    } else {
                        log.info("Successfully sent risk result (RequestID: {}), Offset: {}",
                                requestId, res.getRecordMetadata().offset());
                    }
                });
    }

}
