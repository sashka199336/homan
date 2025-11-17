package com.globus_bank.customer_service.exception;

public class BankDoesNotWorkWithIndividualsException extends RuntimeException {
    
    public BankDoesNotWorkWithIndividualsException(String message) {
        super(message);
    }
}
