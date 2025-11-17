package com.globus_bank.customer_service.controller;

import com.globus_bank.customer_service.dto.response.BankAccountResponseOk;
import com.globus_bank.customer_service.dto.update.BankAccountUpdateDto;
import com.globus_bank.customer_service.service.BankAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class BankAccountController {
    
    private final BankAccountService bankAccountService;
    
    @GetMapping("/{id}/bank-accounts")
    public ResponseEntity<List<BankAccountResponseOk>> getBankAccountsByCustomerId(@PathVariable UUID id) {
        List<BankAccountResponseOk> dtos = bankAccountService.findAllByCustomerId(id);
        return ResponseEntity.ok(dtos);
    }
    
    @PatchMapping("/{customerId}/bank-accounts/{bankAccountsId}")
    public ResponseEntity<?> update(@PathVariable UUID bankAccountsId, @Valid @RequestBody BankAccountUpdateDto dto) {
        bankAccountService.updateBankAccount(bankAccountsId, dto);
        return ResponseEntity.ok().body(dto);
    }
}
