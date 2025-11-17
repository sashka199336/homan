package com.projectbank.loan.dto.subclassForRiskLoanDto.subclass;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Manager {
    private String inn;
    private Fio fio;
    private String post;
    private String hid;
    private String type;
    private Object invalidity;
    @JsonProperty("start_date")
    private Long startDate;
}
