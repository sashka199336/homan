package com.globus_bank.customer_service.entity.enums;

public enum CustomerType {
    
    LEGAL_ENTITY("Legal_entity"),
    
    INDIVIDUAL_ENTREPRENEUR("Individual_entrepreneur"),;
    
    private final String displayName;
    
    CustomerType(String displayName) {
        this.displayName = displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
