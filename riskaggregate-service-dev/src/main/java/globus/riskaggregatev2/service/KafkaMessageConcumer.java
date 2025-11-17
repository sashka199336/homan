package globus.riskaggregatev2.service;

import globus.riskaggregatev2.dto.crosservice.CheckCommandEvent;
import globus.riskaggregatev2.dto.internal.CompanyResponseEvent;
import globus.riskaggregatev2.dto.internal.CompanyRiskResponseEvent;
import globus.riskaggregatev2.dto.internal.CompanyScoringResponseEvent;
import globus.riskaggregatev2.dto.internal.PassportResponseEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class KafkaMessageConcumer {
    private final AggregatorService aggregatorService;

    @KafkaListener(topics = "${app.kafka.topics.check-request}")
    public void handleIncomingRequest(@Payload CheckCommandEvent message) {
        log.info("Received new check request: {}", message.getRequestID());
        aggregatorService.processRequest(message);
    }

    @KafkaListener(topics = "${app.kafka.topics.pass-response}")
    public void handleIncomingPassportResponse(@Payload PassportResponseEvent message) {
        log.info("Recieved passport check results for request: {}", message.getRequestID());
        aggregatorService.processPassportResponse(message);
    }

    @KafkaListener(topics = "${app.kafka.topics.company-response}")
    public void handleIncomingCompanyResponse(@Payload CompanyResponseEvent message) {
        log.info("Recieved company check results for request: {}", message.getRequestID());
        aggregatorService.processCompanyResponse(message);
    }

    @KafkaListener(topics = "${app.kafka.topics.scoring-response}")
    public void handleIncomingScoringResponse(@Payload CompanyScoringResponseEvent message) {
        log.info("Recieved company scoring results for request: {}", message.getRequestID());
        aggregatorService.processScoringResponse(message);
    }

    @KafkaListener(topics = "${app.kafka.topics.risk-response}")
    public void handleIncomingRiskResponse(@Payload CompanyRiskResponseEvent message) {
        log.info("Recieved company risk scoring results for request: {}", message.getRequestID());
        aggregatorService.processRiskResponse(message);
    }

}
