package com.projectbank.loan.service;

import com.projectbank.loan.dto.LoanRiskDto;
import com.projectbank.loan.dto.RiskLoanDto;
import com.projectbank.loan.entity.LoanApplication;
import com.projectbank.loan.kafka.producer.RiskKafkaProducer;
import com.projectbank.loan.mapper.RiskMapper;
import com.projectbank.loan.mongo.LoanValidationRepository;
import com.projectbank.loan.mongo.LoanValidationResultDocument;
import com.projectbank.loan.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class RiskAggregatorService {
    private final RiskKafkaProducer riskKafkaProducer;
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanValidationRepository loanValidationRepository;
    private final RiskMapper riskMapper;
    private final RiskValidator riskValidator;

    public void processRiskRequest(LoanApplication application) {
        riskValidator.validateLoanApplication(application);
        LoanRiskDto requestDto = riskMapper.toRiskRequestDto(application);
        riskKafkaProducer.processRiskRequest(requestDto);
        log.info("Отправлен запрос на проверку: {}", requestDto);
    }

    @Transactional
    public void processRiskResponse(RiskLoanDto responseDto) {
        loanApplicationRepository.findById(responseDto.requestID())
                .ifPresentOrElse(
                        application -> updateApplicationAndValidation(application, responseDto),
                        () -> log.warn("Не найдена заявка по ID {}", responseDto.requestID())
                );
    }

    private void updateApplicationAndValidation(LoanApplication application, RiskLoanDto responseDto) {
        LoanValidationResultDocument validation = loanValidationRepository
                .findByBusinessId(application.getId())
                            .orElseThrow(() -> new IllegalStateException(
                                    "Подтверждающий документ для подачи заявки не найден" + application.getId()
                            ));
        riskMapper.updateEntitiesFromResponse(responseDto, application, validation);

        loanApplicationRepository.save(application);
        if (validation != null) {
            loanValidationRepository.save(validation);
        }
        log.info("Обновлены статусы проверки заявки: {}", application.getId());
    }
}
