package com.globus_bank.customer_service.dto.common;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class DocumentsTypeDto {
    
    private UUID id;
    
    private UUID customerId;
    
    private UUID passportId;
    
    @NotNull(message = "Дата документа не должна быть пустой")
    private LocalDate documentDate;
    
    @NotNull(message = "Копия документа не должна быть пустой")
    private byte[] documentScan;
    
    private Map<String, Object> additionalInformation;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
