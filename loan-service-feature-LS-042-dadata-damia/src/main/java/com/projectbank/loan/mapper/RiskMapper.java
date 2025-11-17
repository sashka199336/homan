package com.projectbank.loan.mapper;

import com.projectbank.loan.dto.LoanRiskDto;
import com.projectbank.loan.dto.RiskLoanDto;
import com.projectbank.loan.entity.LoanApplication;
import com.projectbank.loan.mongo.LoanValidationResultDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RiskMapper {

    default LoanRiskDto toRiskRequestDto(LoanApplication application) {
        return new LoanRiskDto(
                application.getId(),
                application.getInnOrOgrn(),
                List.of(application.getPassportNumber())
        );
    }

    @Mapping(target = "dadataPassportValid", source = "dadataPasssportValid")
    @Mapping(target = "dadataCompanyValid", source = "dadataCompanyValid")
    @Mapping(target = "damiaRiskValid", source = "damiaRiskValid")
    @Mapping(target = "damiaFinanceValid", source = "damiaFinanceValid")
    void updateLoanApplicationFromResponse(RiskLoanDto dto, @MappingTarget LoanApplication entity);

    @Mapping(target = "isPassportValid", source = "dadataPasssportValid")
    @Mapping(target = "isInnValid", source = "dadataCompanyValid")
    @Mapping(target = "isCreditLimitValid", source = "damiaFinanceValid")
    @Mapping(target = "isPaymentHistoryValid", source = "damiaRiskValid")
    void updateLoanValidationFromResponse(RiskLoanDto dto, @MappingTarget LoanValidationResultDocument validation);

    default void updateEntitiesFromResponse(RiskLoanDto dto,
                                            LoanApplication application,
                                            LoanValidationResultDocument validation) {
        updateLoanApplicationFromResponse(dto, application);
        if (validation != null) {
            updateLoanValidationFromResponse(dto, validation);
            validation.setUpdatedAt(LocalDateTime.now());
        }
    }
}