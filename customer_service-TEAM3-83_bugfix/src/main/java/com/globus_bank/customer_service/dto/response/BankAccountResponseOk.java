package com.globus_bank.customer_service.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class BankAccountResponseOk {
    
    private UUID id;
    
    private String settlementAccount;
    
    private String correspondentAccount;
    
    private String bic;
}
