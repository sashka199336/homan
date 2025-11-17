package com.projectbank.loan.dto.subclassForRiskLoanDto.subclass;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CompanyState {
    private String status;
    private String code;
    @JsonProperty("actuality_date")
    private Long actualityDate;
    @JsonProperty("registration_date")
    private Long registrationDate;
    @JsonProperty("liquidation_date")
    private Long liquidationDate;
}
