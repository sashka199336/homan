package com.projectbank.loan.dto.subclassForRiskLoanDto.subclass;

import lombok.Data;

@Data
public class Fio {
    private String surname;
    private String name;
    private String patronymic;
    private String gender;
    private String source;
    private Integer qc;
}
