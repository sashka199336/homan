package globus.riskaggregatev2.service;

import globus.riskaggregatev2.dto.MongoDocument;
import globus.riskaggregatev2.dto.crosservice.CheckCommandEvent;
import globus.riskaggregatev2.dto.crosservice.CheckResultEvent;
import globus.riskaggregatev2.dto.internal.*;
import globus.riskaggregatev2.repository.RequestsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class AggregatorService {
    private final KafkaMessageProducer messageProducer;
    private final RequestsRepository requestsRepository;
    private final MongoTemplate mongoTemplate;

    public void processRequest(CheckCommandEvent message) {
        log.info("Processing request {}", message.getRequestID());
        if (requestsRepository.existsByRequestID(message.getRequestID())) {
            log.error("Request with id {} already exists. Dropping new request.", message.getRequestID());
            return;
        }
        var newResponse = new CheckResultEvent();
        newResponse.setRequestID(message.getRequestID());
        newResponse.setCreationTime(LocalDateTime.now());
        MongoDocument newDocument = new MongoDocument(null, newResponse.getRequestID(), newResponse.getCreationTime(),
                                                      newResponse);
        try {
            requestsRepository.save(newDocument);
            log.info("Saved to database new check template {}", newDocument.getRequestID());
        } catch (Exception e) {
            log.error("Error saving new check template {}", newDocument.getRequestID());
            log.error(e.getMessage(), e);
        }
        messageProducer.sendCompanyRequest(new CompanyRequestEvent(message.getRequestID(), message.getInn()));
        messageProducer.sendPassportRequest(new PassportRequestEvent(message.getRequestID(), message.getPassports()));
        messageProducer.sendScoringRequest(new CompanyScoringRequestEvent(message.getRequestID(), message.getInn()));
        messageProducer.sendRiskRequest(new CompanyRiskRequestEvent(message.getRequestID(), message.getInn(), "_problemCredit"));
        //TODO: захардкоженную риск-модель нужно либо вынести в настройки, либо перенести в запрос либо добавить обрабоку нескольких моделей. Требуется согласование.
    }

    public void processPassportResponse(PassportResponseEvent message) {
        var checkRecord = requestsRepository.findByRequestID(message.getRequestID());
        if (checkRecord.isPresent()) {
            Query query = new Query(Criteria.where("requestID")
                                            .is(message.getRequestID()));
            Update update = new Update().set("checkResponse.passportsCheckResult", message.getCheckedPassports());
            mongoTemplate.updateFirst(query, update, MongoDocument.class);
            log.info("Passport response for requestId {} updated.", message.getRequestID());
        } else {
            log.info("No record found in database requestID {}. Dropping passport data update.",
                     message.getRequestID());
        }
    }

    public void processCompanyResponse(CompanyResponseEvent message) {
        var checkRecord = requestsRepository.findByRequestID(message.getRequestID());
        if (checkRecord.isPresent()) {
            Query query = new Query(Criteria.where("requestID")
                                            .is(message.getRequestID()));
            Update update = new Update().set("checkResponse.companyCheckResult", message.getCompanyCheckResult());
            mongoTemplate.updateFirst(query, update, MongoDocument.class);
            log.info("Company response for requestId {} updated.", message.getRequestID());
        } else {
            log.info("No record found in database requestID {}. Dropping company data update.", message.getRequestID());
        }
    }

    public void processScoringResponse(CompanyScoringResponseEvent message) {
        var checkRecord = requestsRepository.findByRequestID(message.getRequestID());
        if (checkRecord.isPresent()) {
            Query query = new Query(Criteria.where("requestID")
                                            .is(message.getRequestID()));
            Update update = new Update().set("checkResponse.financeScoring", message.getFinancialScoringResponse());
            mongoTemplate.updateFirst(query, update, MongoDocument.class);
            log.info("Finance scoring response for requestId {} updated.", message.getRequestID());
        } else {
            log.info("No record found in database requestID {}. Dropping company data update.", message.getRequestID());
        }
    }

    public void processRiskResponse(CompanyRiskResponseEvent message) {
        var checkRecord = requestsRepository.findByRequestID(message.getRequestID());
        if (checkRecord.isPresent()) {
            Query query = new Query(Criteria.where("requestID")
                                            .is(message.getRequestID()));
            Update update = new Update().set("checkResponse.riskModel", message.getRiskScoringResponse());
            mongoTemplate.updateFirst(query, update, MongoDocument.class);
            log.info("Risk scoring response for requestId {} updated.", message.getRequestID());
        } else {
            log.info("No record found in database requestID {}. Dropping company data update.", message.getRequestID());
        }
    }
}
