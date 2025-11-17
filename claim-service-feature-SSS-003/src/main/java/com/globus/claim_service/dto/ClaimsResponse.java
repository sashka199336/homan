package com.globus.claim_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClaimsResponse {

    private Integer totalPages;
    private Long totalElements;
    private Integer number;
    private Integer size;
    private List<ClaimDto> content;
}
