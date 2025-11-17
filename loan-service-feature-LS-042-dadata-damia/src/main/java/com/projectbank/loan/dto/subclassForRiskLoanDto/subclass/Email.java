package com.projectbank.loan.dto.subclassForRiskLoanDto.subclass;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Email {
    private String value;
    @JsonProperty("unrestricted_value")
    private String unrestrictedValue;
    private EmailData data;
}
