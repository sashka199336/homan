package com.globus_bank.customer_service.dto.update;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.Map;

@Data
public class DocumentsTypeUpdateDto {
    
    @NotNull(message = "Дата документа не должна быть пустой")
    private LocalDate documentDate;
    
    @NotNull(message = "Копия документа не должна быть пустой")
    private byte[] documentScan;
    
    private Map<String, Object> additionalInformation;
}
