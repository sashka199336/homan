package com.projectbank.loan.mongo;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "loan_validation_results")
public class LoanValidationResultDocument {

    @Id
    private UUID id;

    private UUID businessId;

    private Boolean isPassportValid;
    private Boolean isInnValid;
    private Boolean isPaymentHistoryValid;
    private Boolean isCreditLimitValid;
    private Boolean isRiskValid;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
