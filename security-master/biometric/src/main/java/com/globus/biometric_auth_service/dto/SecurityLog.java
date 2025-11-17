package com.globus.biometric_auth_service.dto;

import lombok.Data;

@Data
public class SecurityLog {
    private Long userId;
    private String eventType;
    private Boolean isSuspicious;
    private String reason;
    private String deviceInfo;
}