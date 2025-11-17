package com.projectbank.loan.dto.subclassForRiskLoanDto;

import com.projectbank.loan.dto.subclassForRiskLoanDto.subclass.Suggestion;
import lombok.Data;
import java.util.List;

@Data
public class CompanyCheck {
    private List<Suggestion> suggestions;
}

