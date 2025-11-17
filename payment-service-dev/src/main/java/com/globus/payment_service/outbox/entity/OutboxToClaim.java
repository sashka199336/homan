package com.globus.payment_service.outbox.entity;

import com.globus.payment_service.dto.TransactionCompletionEvent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.UUID;

@Entity
@Table(name = "outbox_to_claim", schema = "payment_schema")
@Getter
@Setter
@NoArgsConstructor

public class OutboxToClaim {

    public OutboxToClaim (TransactionCompletionEvent payload) {
        this.payload = payload;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private TransactionCompletionEvent payload;
}
