package com.projectbank.loan.dto.subclassForRiskLoanDto.subclass;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Phone {
    private String value;
    @JsonProperty("unrestricted_value")
    private String unrestrictedValue;
    private PhoneDetails data;
}
