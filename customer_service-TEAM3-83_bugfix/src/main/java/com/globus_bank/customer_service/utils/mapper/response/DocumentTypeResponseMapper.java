package com.globus_bank.customer_service.utils.mapper.response;

import com.globus_bank.customer_service.dto.response.DocumentTypeResponseOk;
import com.globus_bank.customer_service.entity.DocumentsTypesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface DocumentTypeResponseMapper {
    
    @Mapping(source = "id", target = "documentId")
    @Mapping(source = "additionalInformation", target = "documentType", qualifiedByName = "mapDocumentType")
    DocumentTypeResponseOk toResponse(DocumentsTypesEntity dto);
    
    @Named("mapDocumentType")
    default String mapDocumentType(Map<String, Object> additionalInformation) {
        return (String) additionalInformation.get("type");
    }
}
