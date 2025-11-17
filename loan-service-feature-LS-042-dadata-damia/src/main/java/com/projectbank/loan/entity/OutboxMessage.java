package com.projectbank.loan.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String aggregateType;

    private UUID aggregateId;

    private String type;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;

    private boolean processed;
}
