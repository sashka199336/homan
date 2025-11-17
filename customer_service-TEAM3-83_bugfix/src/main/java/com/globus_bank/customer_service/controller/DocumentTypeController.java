package com.globus_bank.customer_service.controller;

import com.globus_bank.customer_service.dto.response.DocumentTypeResponseOk;
import com.globus_bank.customer_service.dto.update.DocumentsTypeUpdateDto;
import com.globus_bank.customer_service.service.DocumentsTypesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
public class DocumentTypeController {
    
    private final DocumentsTypesService documentsTypesService;
    
    @GetMapping("/{id}/documents-types")
    public ResponseEntity<List<DocumentTypeResponseOk>> getDocumentTypeByCustomerId(@PathVariable UUID id) {
        List<DocumentTypeResponseOk> dto = documentsTypesService.findAllByCustomerId(id);
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/{customerId}/documents-types/{documentId}/scan")
    public ResponseEntity<byte[]> getDocumentScan(
            @PathVariable UUID customerId,
            @PathVariable UUID documentId) {
        
        return ResponseEntity.ok(documentsTypesService.getDocumentScanUrl(customerId, documentId));
    }
    
    @PatchMapping("/{customerId}/documents-types/{documentId}")
    public ResponseEntity<?> update(@PathVariable UUID documentId, @Valid @RequestBody DocumentsTypeUpdateDto dto) {
        documentsTypesService.updateDocumentsType(documentId, dto);
        return ResponseEntity.ok().body(dto);
    }
}
