package com.projectbank.loan.dto;

import java.util.List;
import java.util.UUID;

public record DamiaRequestDto(
        UUID businessId,
        List<String> passports,
        String inn
){}
