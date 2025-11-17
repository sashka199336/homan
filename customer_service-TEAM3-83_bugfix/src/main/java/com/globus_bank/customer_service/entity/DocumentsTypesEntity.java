package com.globus_bank.customer_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDate;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "documents_types")
public class DocumentsTypesEntity extends BaseEntity {
    
    private LocalDate documentDate;
    
    @Column(name = "details", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> additionalInformation;
    
    @Lob
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] documentScan;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private CustomerEntity customer;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "passport_id", referencedColumnName = "id")
    private PassportEntity passport;
}
