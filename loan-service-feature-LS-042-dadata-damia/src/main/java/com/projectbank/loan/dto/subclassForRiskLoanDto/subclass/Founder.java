package com.projectbank.loan.dto.subclassForRiskLoanDto.subclass;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Founder {
    private String ogrn;
    private String inn;
    private String name;
    private String hid;
    private String type;
    private Share share;
    private Object invalidity;
    @JsonProperty("start_date")
    private Long startDate;
}
