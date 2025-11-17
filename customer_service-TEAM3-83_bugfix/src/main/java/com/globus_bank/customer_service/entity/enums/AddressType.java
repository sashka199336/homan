package com.globus_bank.customer_service.entity.enums;

public enum AddressType {
    
    LEGAL("Legal"),
    
    ACTUAL("Actual"),
    
    REGISTRATION("Registration"),
    
    RESIDENTIAL("Residential");
    
    private final String displayName;
    
    AddressType(String displayName) {
        this.displayName = displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
