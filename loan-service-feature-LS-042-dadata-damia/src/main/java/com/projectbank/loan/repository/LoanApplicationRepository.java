package com.projectbank.loan.repository;

import com.projectbank.loan.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, UUID> {

    @Query("select l from LoanApplication l where l.innOrOgrn = :innOrOgrn")
    Optional<LoanApplication> findByInnOrOgrn(@Param("innOrOgrn") String innOrOgrn);
}
