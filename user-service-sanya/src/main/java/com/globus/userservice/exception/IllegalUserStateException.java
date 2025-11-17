package com.globus.userservice.exception;

public class IllegalUserStateException extends RuntimeException {
    public IllegalUserStateException(String message) {
        super(message);
    }
}
