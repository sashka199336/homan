package com.globus_bank.customer_service.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class LegalResponseOk {

    private UUID legalEntityId;

    private String companyName;

    private String ogrn;

    private String kpp;

    private String position;
}
