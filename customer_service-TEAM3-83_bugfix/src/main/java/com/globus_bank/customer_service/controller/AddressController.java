package com.globus_bank.customer_service.controller;

import com.globus_bank.customer_service.dto.response.AddressResponseOk;
import com.globus_bank.customer_service.dto.update.AddressUpdateDto;
import com.globus_bank.customer_service.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class AddressController {
    
    private final AddressService addressService;
    
    @GetMapping("/{id}/addresses")
    public ResponseEntity<List<AddressResponseOk>> getAddressesByCustomerId(@PathVariable UUID id) {
        List<AddressResponseOk> addresses = addressService.findAllByCustomerId(id);
        return ResponseEntity.ok(addresses);
    }
    
    @PatchMapping("/{customerId}/addresses/{addressId}")
    public ResponseEntity<?> update(@PathVariable UUID addressId, @Valid @RequestBody AddressUpdateDto dto) {
        addressService.updateAddress(addressId, dto);
        return ResponseEntity.ok().body(dto);
    }
}
