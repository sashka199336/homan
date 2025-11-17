package com.globus_bank.customer_service.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class IndividualEntrepreneurResponseOk {

    private UUID individualEntrepreneurId;

    private String companyName;

    private String ogrnip;
}
