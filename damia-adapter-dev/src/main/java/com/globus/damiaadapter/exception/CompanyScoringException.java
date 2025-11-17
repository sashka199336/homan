package com.globus.damiaadapter.exception;

public class CompanyScoringException extends RuntimeException {
    public CompanyScoringException(String message) {
        super(message);
    }

    public CompanyScoringException(String message, Throwable cause) {
        super(message, cause);
    }
}
