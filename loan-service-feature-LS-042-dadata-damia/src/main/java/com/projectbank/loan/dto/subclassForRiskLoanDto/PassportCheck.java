package com.projectbank.loan.dto.subclassForRiskLoanDto;

import lombok.Data;

@Data
public class PassportCheck {
    private String source; // проверяемая строка
    private String series; // выделенная серия паспорта
    private String number; // выделенный номер паспорта
    private int qc; // результат проверки: 0 - действующий
    //                                     1 - неверный формат серии или номера
    //                                     2 - пустое исходное значение
    //                                     10 - паспорт недействителен
}