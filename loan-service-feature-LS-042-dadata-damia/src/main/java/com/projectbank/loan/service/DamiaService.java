package com.projectbank.loan.service;

import com.projectbank.loan.dto.DamiaResponseDto;
import com.projectbank.loan.entity.LoanApplication;
import com.projectbank.loan.mapper.DamiaMapper;
import com.projectbank.loan.mongo.LoanValidationRepository;
import com.projectbank.loan.mongo.LoanValidationResultDocument;
import com.projectbank.loan.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DamiaService {
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanValidationRepository loanValidationRepository;
    private final DamiaMapper damiaMapper;
    private final DamiaValidator damiaValidator;

    @Transactional
    public void processDamiaResponse(DamiaResponseDto dto) {
        damiaValidator.validateResponse(dto);
        UUID applicationId = dto.businessId();

        if (applicationId == null) {
            log.warn("Не удается обработать: айди не существует");
            return;
        }

        var applicationOpt = loanApplicationRepository.findById(applicationId);
        var validationOpt = loanValidationRepository.findByBusinessId(applicationId);

        updateLoanApplication(applicationOpt, dto, applicationId);
        updateLoanValidationDocument(validationOpt, dto, applicationId);
    }


    private void updateLoanApplication(Optional<LoanApplication> applicationOpt, DamiaResponseDto dto,  UUID applicationId) {
        applicationOpt.ifPresentOrElse(application -> {
            damiaMapper.updateLoanApplication(application, dto);
            loanApplicationRepository.save(application);
            log.info("Обновленна заявка с ID: {}", applicationId);
        }, () -> log.warn("Заявка c ID {} не найдена", applicationId));
    }

    private void updateLoanValidationDocument(Optional<LoanValidationResultDocument> validationOpt, DamiaResponseDto dto, UUID applicationId) {
        validationOpt.ifPresentOrElse(validation -> {
            damiaMapper.updateValidationDocument(validation, dto);
            loanValidationRepository.save(validation);
            log.info("Обновлен документ для ID: {}", applicationId);
        }, () -> {
            LoanValidationResultDocument newDoc = damiaMapper.createNewValidation(applicationId, dto);
            loanValidationRepository.save(newDoc);
            log.info("Создан новый документ для ID: {}", applicationId);
        });
    }
}
