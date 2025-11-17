package globus.riskaggregatev2.service;

import globus.riskaggregatev2.dto.crosservice.CheckResultEvent;
import globus.riskaggregatev2.dto.internal.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaMessageProducer {
    private final KafkaTemplate<String, PassportRequestEvent> sendPassportRequest;
    private final KafkaTemplate<String, CompanyRequestEvent> sendCompanyRequest;
    private final KafkaTemplate<String, CheckResultEvent> sendCheckResponse;
    private final KafkaTemplate<String, CompanyScoringRequestEvent> sendScoringRequest;
    private final KafkaTemplate<String, CompanyRiskRequestEvent> sendRiskRequest;
    private final String passRequestTopic;
    private final String companyRequestTopic;
    private final String checkResponseTopic;
    private final String scoringRequestTopic;
    private final String riskRequestTopic;

    public KafkaMessageProducer(KafkaTemplate<String, PassportRequestEvent> sendPassportRequest,
                                KafkaTemplate<String, CompanyRequestEvent> sendCompanyRequest,
                                KafkaTemplate<String, CheckResultEvent> sendCheckResponse,
                                KafkaTemplate<String, CompanyScoringRequestEvent> sendScoringRequest,
                                KafkaTemplate<String, CompanyRiskRequestEvent> sendRiskRequest,
                                @Value("${app.kafka.topics.pass-request}") String passRequestTopic,
                                @Value("${app.kafka.topics.company-request}") String companyRequestTopic,
                                @Value("${app.kafka.topics.check-response}") String checkResponseTopic,
                                @Value("${app.kafka.topics.scoring-request}") String scoringRequestTopic,
                                @Value("${app.kafka.topics.risk-request}") String riskRequestTopic){
        this.sendPassportRequest = sendPassportRequest;
        this.sendCompanyRequest = sendCompanyRequest;
        this.sendCheckResponse = sendCheckResponse;
        this.sendScoringRequest = sendScoringRequest;
        this.sendRiskRequest = sendRiskRequest;
        this.passRequestTopic = passRequestTopic;
        this.companyRequestTopic = companyRequestTopic;
        this.checkResponseTopic = checkResponseTopic;
        this.scoringRequestTopic = scoringRequestTopic;
        this.riskRequestTopic = riskRequestTopic;
    }

    public void sendPassportRequest(PassportRequestEvent request) {
        sendPassportRequest.send(passRequestTopic, request);
        log.info("Sent passport request to adapter: {}", request.getRequestID());
    }

    public void sendCompanyRequest(CompanyRequestEvent request) {
        sendCompanyRequest.send(companyRequestTopic, request);
        log.info("Sent company request to adapter: {}", request.getRequestID());
    }

    public void sendScoringRequest(CompanyScoringRequestEvent request) {
        sendScoringRequest.send(scoringRequestTopic, request);
        log.info("Sent scoring request to adapter: {}", request.getRequestID());
    }

    public void sendCheckResponse(CheckResultEvent checkResult) {
        sendCheckResponse.send(checkResponseTopic, checkResult);
        log.info("Sent check request to external service: {}", checkResult.getRequestID());
    }

    public void sendRiskRequest(CompanyRiskRequestEvent request) {
        sendRiskRequest.send(riskRequestTopic,request);
        log.info("Sent risk request to adapter: {}", request.getRequestID());
    }
}
