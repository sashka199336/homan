package com.projectbank.loan.dto.subclassForRiskLoanDto.subclass;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Document {
    private String type;
    private String series;
    private String number;
    @JsonProperty("issue_date")
    private Long issueDate;
    @JsonProperty("issue_authority")
    private String issueAuthority;
}
