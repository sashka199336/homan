package com.globus.biometric_auth_service.dto;

import lombok.Data;

@Data
public class SecurityLogDTO {
    private Long userId;
    private Boolean isSuspicious;
    private String reason;


    public SecurityLogDTO() {}

    public SecurityLogDTO(Long userId, Boolean isSuspicious, String reason) {
        this.userId = userId;
        this.isSuspicious = isSuspicious;
        this.reason = reason;
    }
}