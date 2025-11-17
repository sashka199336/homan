package com.globus_bank.customer_service.controller;

import com.globus_bank.customer_service.dto.response.LegalResponseOk;
import com.globus_bank.customer_service.dto.update.LegalEntityUpdateDto;
import com.globus_bank.customer_service.service.LegalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class LegalController {
    
    private final LegalService legalService;
    
    @GetMapping("/{id}/legal-entities")
    public ResponseEntity<LegalResponseOk> getLegalEntityByCustomerId(@PathVariable UUID id) {
        LegalResponseOk legalResponseOk = legalService.findByCustomerId(id);
        return ResponseEntity.ok(legalResponseOk);
    }
    
    @PatchMapping("/{customerId}/legal-entities/{legalId}")
    public ResponseEntity<?> update(@PathVariable UUID legalId, @Valid @RequestBody LegalEntityUpdateDto dto) {
        legalService.updateLegalEntity(legalId, dto);
        return ResponseEntity.ok().body(dto);
    }
}
