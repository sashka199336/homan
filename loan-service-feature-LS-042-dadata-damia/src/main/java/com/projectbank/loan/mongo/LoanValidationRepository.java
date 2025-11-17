package com.projectbank.loan.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface LoanValidationRepository extends MongoRepository<LoanValidationResultDocument, UUID> {
    Optional<LoanValidationResultDocument> findByBusinessId(UUID businessId);
}