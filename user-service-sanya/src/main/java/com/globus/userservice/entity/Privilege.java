package com.globus.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "privileges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Privilege {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "privilege_id", updatable = false, nullable = false)
    private UUID privilegeId;

    @Column(name = "privilege_name", nullable = false, length = 100)
    private String name;

    @Column(name = "access_type", length = 50)
    private String accessType;

    @Column(name = "service", nullable = false, length = 50)
    private String service;

    @Column(name = "description")
    private String description;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}