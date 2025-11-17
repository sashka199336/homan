package com.globus.claim_service.dto;

import com.globus.claim_service.model.enums.ClaimStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@Schema(description = "Параметры фильтрации и пагинации заявок")
public class ClaimRequestParams {
    @Schema(description = "Номер страницы (по умолчанию 0)", example = "0")
    private Integer page = 0;

    @Schema(description = "Размер страницы (по умолчанию 10)", example = "10")
    private Integer size = 10;

    @Schema(description = "Статус заявки", example = "PENDING")
    private String claimStatus;

    public ClaimFilter toFilter(UUID customerId) {
        return new ClaimFilter(
                customerId,
                claimStatus != null ? ClaimStatus.valueOf(claimStatus) : null
        );
    }
}
