package com.projectbank.loan.dto.subclassForRiskLoanDto.subclass;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class Address {
    @JsonProperty("postal_code")
    private String postalCode;
    private String country;
    @JsonProperty("country_iso_code")
    private String countryIsoCode;
    @JsonProperty("federal_district")
    private String federalDistrict;
    private List<Metro> metro;
}
