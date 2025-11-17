package com.globus_bank.customer_service.controller;

import com.globus_bank.customer_service.dto.response.IndividualEntrepreneurResponseOk;
import com.globus_bank.customer_service.dto.update.IndividualEntrepreneurUpdateDto;
import com.globus_bank.customer_service.service.IndividualEntrepreneurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class IndividualEntrepreneurController {
    
    private final IndividualEntrepreneurService individualEntrepreneurService;
    
    @GetMapping("/{id}/individual-entrepreneurs")
    public ResponseEntity<IndividualEntrepreneurResponseOk> getIndividualEntrepreneurByCustomerId(@PathVariable UUID id) {
        IndividualEntrepreneurResponseOk dto = individualEntrepreneurService.findByCustomerId(id);
        return ResponseEntity.ok(dto);
    }
    
    @PatchMapping("/{customerId}/individual-entrepreneurs/{ieId}")
    public ResponseEntity<?> update(@PathVariable UUID ieId, @Valid @RequestBody IndividualEntrepreneurUpdateDto dto) {
        individualEntrepreneurService.updateIndividualEntrepreneur(ieId, dto);
        return ResponseEntity.ok().body(dto);
    }
}
