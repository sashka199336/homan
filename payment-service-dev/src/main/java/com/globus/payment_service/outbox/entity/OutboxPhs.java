package com.globus.payment_service.outbox.entity;

import com.globus.payment_service.dto.DemandForPaymentEventToAccountService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.UUID;

@Entity
@Table(name = "outbox_phs", schema = "payment_schema")
@Getter
@Setter
@NoArgsConstructor
public class OutboxPhs {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private DemandForPaymentEventToAccountService payload;
}