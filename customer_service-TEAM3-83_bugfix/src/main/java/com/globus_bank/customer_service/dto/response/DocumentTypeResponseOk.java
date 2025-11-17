package com.globus_bank.customer_service.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class DocumentTypeResponseOk {
    
    private UUID documentId;
    
    private LocalDate documentDate;
    
    private String documentType;
}
