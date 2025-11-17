package com.globus.payment_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transaction_history", schema = "payment_schema")
@Getter
@Setter
@NoArgsConstructor
public class TransactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id")
    private UUID transactionId;
    @Column(name = "claim_id")
    private UUID claimId;
    @Column(name = "to_bill", columnDefinition = "VARCHAR(20)", nullable = false)
    private String toBill;
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    @Column(name = "create_date_time")
    @CreationTimestamp
    private Instant createDateTime;
    @Column(name = "update_date_time")
    @UpdateTimestamp
    private Instant updateDateTime;
    @Column(name = "status", columnDefinition = "VARCHAR(15)")
    @Enumerated(EnumType.STRING)
    private Status status;
}
