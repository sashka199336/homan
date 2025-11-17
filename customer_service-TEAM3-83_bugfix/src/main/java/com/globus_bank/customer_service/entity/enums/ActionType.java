package com.globus_bank.customer_service.entity.enums;

public enum ActionType {
    
    UPDATE("Update"),
    
    DELETE("Delete"),
    
    VIEW("View"),
    
    EXPORT("Export"),
    
    IMPORT("Import"),
    
    CREATE("Create"),
    
    STATUS_CHANGE("Status_change");
    
    private final String displayName;
    
    ActionType(String displayName) {
        this.displayName = displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
