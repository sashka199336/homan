package com.globus.claim_service.repository;

import com.globus.claim_service.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, UUID>, JpaSpecificationExecutor<Claim> {

    @Query("SELECT c FROM Claim c WHERE c.customerId = :customerId")
    List<Claim> findClaimsByUserId(@Param("userId") UUID customerId);

    @Modifying
    @Query("UPDATE Claim c SET c.status = :status, c.updatedAt = :updatedAt WHERE c.id = :claimId")
    void updateStatus(
            @Param("claimId") UUID claimId,
            @Param("status") String status,
            @Param("updatedAt") Instant updatedAt
    );

    @Modifying
    @Query("UPDATE Claim c SET c.toBill = :toBill, c.status = :status, c.updatedAt = :updatedAt WHERE c.id = :claimId")
    void updateAccountStatus(
            @Param("claimId") UUID claimId,
            @Param("status") String status,
            @Param("toBill") String toBill,
            @Param("updatedAt") Instant updatedAt
    );
}
