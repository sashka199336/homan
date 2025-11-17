package com.projectbank.loan.service;

import com.projectbank.loan.dto.DamiaResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class DamiaValidator {

    public void validateResponse(DamiaResponseDto dto) {
        UUID applicationId = dto.businessId();
        if (applicationId == null) {
            throw new IllegalArgumentException("businessId не может быть null");
        }
    }
}
