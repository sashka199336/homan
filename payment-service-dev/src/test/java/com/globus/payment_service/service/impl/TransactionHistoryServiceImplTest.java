package com.globus.payment_service.service.impl;

import com.globus.payment_service.AbstractTest;
import com.globus.payment_service.dto.NeedForPaymentEventFromLoanSystem;
import com.globus.payment_service.dto.FundsTransferCompletionEvent;
import com.globus.payment_service.entity.Status;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionHistoryServiceImplTest extends AbstractTest {

    @Test
    @Order(1)
    void whenFullMessage_thenOk() {

        assertTrue(transactionHistoryRepository.findAll().isEmpty());
        System.setOut(new PrintStream(outputStreamCaptor));

        transactionService.startTransaction(fullMessageFromLoan);
        System.setOut(standardOut);
        assertEquals(1, transactionHistoryRepository.findAll().size());
        assertEquals(Status.PENDING, transactionHistoryRepository.findAll().get(0).getStatus());
        transactionId = transactionHistoryRepository.findAll().get(0).getTransactionId();
        assertTrue(outputStreamCaptor.toString().contains("Created transactionHistory record Id " + transactionId +
                " for claim Id 4d3669de-4a72-4d92-8709-3bf339a8c762"));
    }

    @Test
    @Order(2)
    void whenMissedField_thenError() {
        NeedForPaymentEventFromLoanSystem message = NeedForPaymentEventFromLoanSystem.builder()
                .amount(BigDecimal.valueOf(200000))
                .claimId(UUID.fromString("4d3669de-4a72-4d92-8709-3bf339a8c762"))
                .build();
        assertTrue(transactionHistoryRepository.findAll().isEmpty());
        System.setOut(new PrintStream(outputStreamCaptor));

        transactionService.startTransaction(message);
        System.setOut(standardOut);
        entityManager.clear();
        assertTrue(transactionHistoryRepository.findAll().isEmpty());
        assertTrue(outputStreamCaptor.toString().contains(
                "Failed to create database record for claim Id 4d3669de-4a72-4d92-8709-3bf339a8c762"));
    }

    @Test
    @Order(3)
    void whenSuccessFinalizeTransaction_thenOk() {
        transactionService.startTransaction(fullMessageFromLoan);
        assertEquals(1, transactionHistoryRepository.findAll().size());
        transactionId = transactionHistoryRepository.findAll().get(0).getTransactionId();
        FundsTransferCompletionEvent message = FundsTransferCompletionEvent.builder()
                .transactionId(transactionId)
                .status(Status.DONE)
                .build();
        System.setOut(new PrintStream(outputStreamCaptor));

        transactionService.finalizeTransaction(message);
        System.setOut(standardOut);
        assertTrue(outputStreamCaptor.toString().contains(
                "TransactionHistory Id " + transactionId + " processing completed at "));
        assertEquals(Status.DONE, transactionHistoryRepository.findById(transactionId).get().getStatus());
    }
}