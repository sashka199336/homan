package com.projectbank.loan.mapper;

import com.projectbank.loan.dto.DamiaResponseDto;
import com.projectbank.loan.entity.LoanApplication;
import com.projectbank.loan.mongo.LoanValidationResultDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.time.LocalDateTime;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface DamiaMapper {

    @Mapping(target = "damiaFinanceValid", expression = "java(calculateFinanceValid(dto))")
    @Mapping(target = "damiaRiskValid", expression = "java(calculateRiskValid(dto))")
    void updateLoanApplication(@MappingTarget LoanApplication application, DamiaResponseDto dto);

    @Mapping(target = "isInnValid", expression = "java(calculateFinanceValid(dto))")
    @Mapping(target = "isRiskValid", expression = "java(calculateRiskValid(dto))")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateValidationDocument(@MappingTarget LoanValidationResultDocument document, DamiaResponseDto dto);

    default LoanValidationResultDocument createNewValidation(UUID businessId, DamiaResponseDto dto) {
        boolean isInnValid = dto != null && calculateFinanceValid(dto);
        boolean isRiskValid = dto != null && calculateRiskValid(dto);

        return LoanValidationResultDocument.builder()
                .businessId(businessId)
                .isInnValid(isInnValid)
                .isRiskValid(isRiskValid)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    default boolean calculateFinanceValid(DamiaResponseDto dto) {
        if (dto == null) return false;
        return !dto.bankrots2016() && !dto.problemCredit();
    }

    default boolean calculateRiskValid(DamiaResponseDto dto) {
        if (dto == null) return false;
        return !dto.tech115fz() && !dto.techLiquidity()
                && !dto.finAutonomy() && !dto.salesProfitability();
    }
}
