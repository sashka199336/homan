package com.projectbank.loan.dto;

import java.util.UUID;

public record DamiaResponseDto(
        UUID businessId,
        boolean bankrots2016,
        boolean tech115fz,
        boolean problemCredit,
        boolean techLiquidity,
        boolean finAutonomy,
        boolean salesProfitability
) {}
