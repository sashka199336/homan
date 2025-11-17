package com.projectbank.loan.mapper;

import com.projectbank.loan.entity.LoanApplication;
import com.projectbank.loan.mongo.LoanValidationResultDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanValidationMapper {
    @Mapping(target = "businessId", source = "id")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    LoanValidationResultDocument toInitialValidation(LoanApplication application);
}

