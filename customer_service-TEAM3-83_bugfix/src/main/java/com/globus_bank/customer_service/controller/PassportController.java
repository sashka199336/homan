package com.globus_bank.customer_service.controller;

import com.globus_bank.customer_service.dto.response.PassportResponseOk;
import com.globus_bank.customer_service.dto.update.PassportUpdateDto;
import com.globus_bank.customer_service.service.PassportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class PassportController {
    
    private final PassportService passportService;
    
    @GetMapping("/{customerId}/documents-types/{documentId}/passport")
    public ResponseEntity<PassportResponseOk> getPassportByDocument(
            @PathVariable UUID customerId,
            @PathVariable UUID documentId) {
        
        PassportResponseOk document = passportService.findByDocumentId(documentId, customerId);
        
        return ResponseEntity.ok(document);
    }
    
    @PatchMapping("/{customerId}/documents-types/{documentId}/passport/{passportId}")
    public ResponseEntity<?> update(@PathVariable UUID passportId, @Valid @RequestBody PassportUpdateDto dto) {
        passportService.updatePassport(passportId, dto);
        return ResponseEntity.ok().body(dto);
    }
}
