package com.globus_bank.customer_service.exception;

public class InvalidClientTypeException extends RuntimeException {
    
    public InvalidClientTypeException(String message) {
        super(message);
    }
}
