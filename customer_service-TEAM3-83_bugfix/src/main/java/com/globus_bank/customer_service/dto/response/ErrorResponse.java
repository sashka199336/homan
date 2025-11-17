package com.globus_bank.customer_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    
    private String error;
    
    private String uuid;
    
    private String time;
    
    private String message;
}
