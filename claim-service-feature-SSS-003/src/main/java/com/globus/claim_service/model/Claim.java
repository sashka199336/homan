package com.globus.claim_service.model;

import com.globus.claim_service.model.enums.ClaimStatus;
import com.globus.claim_service.model.enums.ClaimType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Table(name = "claim", schema = "claim_schema")
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "claim_id", columnDefinition = "UUID")
    private UUID claimId;
    @Column(name = "customer_id")
    private UUID customerId;
    @Column(name = "claim_type")
    @ToString.Exclude
    private ClaimType claimType;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "credit_term_month")
    private Integer creditTermMonth;
    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
    @Enumerated(EnumType.STRING)
    @ToString.Exclude
    @Column(name = "claim_status")
    private ClaimStatus status;
    @Column(name = "to_bill")
    private String toBill;

    @Override
    public String toString() {
        return "Claim{" +
                "claimId=" + claimId +
                ", customerId=" + customerId +
                ", claimType=" + claimType +
                ", amount=" + amount +
                ", creditTermMonth=" + creditTermMonth +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", status=" + status +
                ", toBill='" + toBill + '\'' +
                '}';
    }
}
