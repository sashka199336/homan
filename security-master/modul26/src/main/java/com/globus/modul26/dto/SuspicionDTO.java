package com.globus.modul26.dto;

import lombok.Data;

@Data
public class SuspicionDTO {
    private Long userId;
    private Boolean suspicion;
    private String reason;



    public SuspicionDTO() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Boolean getSuspicion() { return suspicion; }
    public void setSuspicion(Boolean suspicion) { this.suspicion = suspicion; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}