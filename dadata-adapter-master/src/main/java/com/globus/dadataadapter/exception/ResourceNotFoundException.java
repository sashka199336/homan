package com.globus.dadataadapter.exception;

public class ResourceNotFoundException extends RuntimeException {

    private static final String RESOURCE_NOT_FOUND_MESSAGE = "{} with {}: {} not found";

    public ResourceNotFoundException(String resource, String property, String value) {
        super(String.format(RESOURCE_NOT_FOUND_MESSAGE, resource, property, value));
    }

}
