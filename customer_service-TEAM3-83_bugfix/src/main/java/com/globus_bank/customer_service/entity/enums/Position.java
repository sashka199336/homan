package com.globus_bank.customer_service.entity.enums;

public enum Position {
    
    CEO("CEO"),
    
    FOUNDER("Founder");
    
    private final String displayName;
    
    Position(String displayName) {
        this.displayName = displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
