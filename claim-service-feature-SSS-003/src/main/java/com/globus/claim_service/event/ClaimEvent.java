package com.globus.claim_service.event;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClaimEvent {
    private UUID claimId;
    private String status;
    private String message;
}
