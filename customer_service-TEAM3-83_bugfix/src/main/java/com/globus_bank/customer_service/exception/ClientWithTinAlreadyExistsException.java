package com.globus_bank.customer_service.exception;

public class ClientWithTinAlreadyExistsException extends RuntimeException {
    
    public ClientWithTinAlreadyExistsException(String message) {
        super(message);
    }
}
