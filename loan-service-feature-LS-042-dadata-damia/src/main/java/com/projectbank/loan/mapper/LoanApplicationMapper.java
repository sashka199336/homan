package com.projectbank.loan.mapper;

import com.projectbank.loan.dto.ClaimDocumentPackageDto;
import com.projectbank.loan.entity.LoanApplication;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LoanApplicationMapper {
    LoanApplicationMapper INSTANCE = Mappers.getMapper(LoanApplicationMapper.class);

    ClaimDocumentPackageDto toDto(LoanApplication entity);

    LoanApplication toEntity(ClaimDocumentPackageDto dto);

    void updateEntityFromDto(ClaimDocumentPackageDto dto, @MappingTarget LoanApplication entity);
}
