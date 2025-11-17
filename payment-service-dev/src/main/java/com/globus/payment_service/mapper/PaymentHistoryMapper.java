package com.globus.payment_service.mapper;

import com.globus.payment_service.dto.DemandForPaymentEventToAccountService;
import com.globus.payment_service.dto.NeedForPaymentEventFromLoanSystem;
import com.globus.payment_service.dto.TransactionCompletionEvent;
import com.globus.payment_service.entity.TransactionHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentHistoryMapper {

    @Mapping(target = "status", expression = "java(com.globus.payment_service.entity.Status.PENDING)")
    TransactionHistory fromLoanMessageToEntity (NeedForPaymentEventFromLoanSystem message);

    DemandForPaymentEventToAccountService fromEntityToOutboxBody (TransactionHistory entity);

    @Mapping(target = "claimStatus", source = "status")
    TransactionCompletionEvent fromEntityToClaimMessage (TransactionHistory entity);
}
