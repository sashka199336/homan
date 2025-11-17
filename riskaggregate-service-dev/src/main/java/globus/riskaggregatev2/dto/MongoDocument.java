package globus.riskaggregatev2.dto;

import globus.riskaggregatev2.dto.crosservice.CheckResultEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "pendingRequests")
@AllArgsConstructor
@Getter
public class MongoDocument {
    @Id
    private String id;
    @Indexed(unique = true)
    private UUID requestID;
    private LocalDateTime timestamp;
    private CheckResultEvent checkResponse;
}
