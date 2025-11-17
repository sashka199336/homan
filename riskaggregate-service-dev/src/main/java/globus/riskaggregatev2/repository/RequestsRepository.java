package globus.riskaggregatev2.repository;

import globus.riskaggregatev2.dto.MongoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequestsRepository extends MongoRepository<MongoDocument, String> {
    Optional<MongoDocument> findByRequestID(UUID uuid);

    List<MongoDocument> findByTimestampBefore(LocalDateTime cutoff);

    void deleteByRequestID(UUID uuid);

    boolean existsByRequestID(UUID uuid);
}
