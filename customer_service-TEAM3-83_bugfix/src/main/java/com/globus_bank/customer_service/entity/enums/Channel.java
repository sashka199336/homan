package com.globus_bank.customer_service.entity.enums;

public enum Channel {
    
    WEB("Web"),
    
    EMAIL("e-mail"),
    
    SMS("sms"),
    
    PUSH("Push");

    private final String displayName;

    Channel(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
