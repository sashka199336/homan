package globus.riskaggregatev2.service;

import globus.riskaggregatev2.dto.MongoDocument;
import globus.riskaggregatev2.repository.RequestsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@EnableScheduling
public class ResponseSender {
    private final KafkaMessageProducer messageProducer;
    private final RequestsRepository database;
    private final Integer ttlMinutes;

    public ResponseSender(@Value("${app.check-ttl-minutes:5}") Integer ttlMinutes, RequestsRepository database,
                          KafkaMessageProducer messageProducer) {
        this.ttlMinutes = ttlMinutes;
        this.database = database;
        this.messageProducer = messageProducer;
    }

    @Scheduled(fixedDelay = 10000L)
    public void sendByTtl() {
        LocalDateTime cutoff = LocalDateTime.now()
                                            .minusMinutes(ttlMinutes);
        var allDocuments = database.count();
        List<MongoDocument> expiredDocuments = database.findByTimestampBefore(cutoff);
        log.info("Total documents in database: {}", allDocuments);
        log.info("Found {} expired documents", expiredDocuments.size());
        for (var document : expiredDocuments) {
            messageProducer.sendCheckResponse(document.getCheckResponse());
            database.deleteByRequestID(document.getRequestID());
            log.info("Removed expired document {}", document.getRequestID());
        }
    }
}
