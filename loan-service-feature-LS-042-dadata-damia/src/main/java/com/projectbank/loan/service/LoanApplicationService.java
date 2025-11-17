package com.projectbank.loan.service;

import com.projectbank.loan.dto.ClaimDocumentPackageDto;
import com.projectbank.loan.entity.LoanApplication;
import com.projectbank.loan.entity.OutboxMessage;
import com.projectbank.loan.mapper.LoanApplicationMapper;
import com.projectbank.loan.mongo.LoanValidationRepository;
import com.projectbank.loan.repository.LoanApplicationRepository;
import com.projectbank.loan.repository.OutboxMessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class LoanApplicationService {
    private final LoanApplicationMapper mapper;
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanValidationRepository loanValidationRepository;
    private final OutboxService outboxService;
    private final OutboxMessageRepository outboxMessageRepository;

    @Transactional
    public void saveNewLoanApplication(ClaimDocumentPackageDto claimDocumentPackageDto) {
        LoanApplication entity = mapper.toEntity(claimDocumentPackageDto);
        log.info("Получен новый запрос: {}", claimDocumentPackageDto);
        entity = loanApplicationRepository.save(entity);
        createInitialValidationDocument(entity);
        OutboxMessage message = outboxService.buildRiskRequestMessage(entity);
        outboxMessageRepository.save(message);
    }

    private void createInitialValidationDocument(LoanApplication application) {
    }

    public void validateApplication(LoanApplication application) {

        //будет обращение в RiskAggregatorService, PaymentHistoryService
    }
}
