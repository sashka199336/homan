package com.projectbank.loan.dto.subclassForRiskLoanDto.subclass;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CompanyDocuments {
    @JsonProperty("fts_registration")
    private Document ftsRegistration;
    @JsonProperty("fts_report")
    private Document ftsReport;
    @JsonProperty("pf_registration")
    private Document pfRegistration;
    @JsonProperty("sif_registration")
    private Document sifRegistration;
    private Document smb;
}
