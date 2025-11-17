package com.globus_bank.customer_service.entity.enums;

public enum Status {
    
    ACTIVE("Active"),
    
    BLOCKED("Blocked"),
    
    DELETED("Deleted");
    
    private final String displayName;
    
    Status(String displayName) {
        this.displayName = displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
