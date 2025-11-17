package com.globus_bank.customer_service.controller;

import com.globus_bank.customer_service.dto.common.ContactsDto;
import com.globus_bank.customer_service.dto.update.ContactsUpdateDto;
import com.globus_bank.customer_service.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class ContactController {
    
    private final ContactService contactService;
    
    @GetMapping("/{id}/contacts")
    public ResponseEntity<List<ContactsDto>> getContactsByCustomerId(@PathVariable UUID id) {
        List<ContactsDto> contacts = contactService.findAllByCustomerId(id);
        return ResponseEntity.ok(contacts);
    }
    
    @PatchMapping("/{customerId}/contacts/{contactsId}")
    public ResponseEntity<?> update(@PathVariable UUID contactsId, @Valid @RequestBody ContactsUpdateDto dto) {
        contactService.updateContacts(contactsId, dto);
        return ResponseEntity.ok().body(dto);
    }
}
