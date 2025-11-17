package com.globus_bank.customer_service.controller;

import com.globus_bank.customer_service.dto.common.FullCustomerDto;
import com.globus_bank.customer_service.dto.response.CustomerCreatedResponse;
import com.globus_bank.customer_service.dto.response.CustomerProfileDto;
import com.globus_bank.customer_service.dto.response.CustomerResponseOk;
import com.globus_bank.customer_service.dto.update.CustomerUpdateDto;
import com.globus_bank.customer_service.entity.CustomerEntity;
import com.globus_bank.customer_service.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    
    private final CustomerService customerService;
    
    @PostMapping
    public ResponseEntity<CustomerCreatedResponse> create(@RequestBody @Validated FullCustomerDto fullCustomerDto) {
        CustomerEntity newCustomer = customerService.saveFullCustomer(fullCustomerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CustomerCreatedResponse(newCustomer.getId()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseOk> get(@PathVariable UUID id) {
        CustomerResponseOk customerDto = customerService.findById(id);
        return ResponseEntity.ok(customerDto);
    }
    
    @GetMapping("/customer-profile/{id}")
    public ResponseEntity<CustomerProfileDto> getCustomerProfile(@PathVariable UUID id) {
        CustomerProfileDto customerProfileDto = customerService.getCustomerProfile(id);
        return ResponseEntity.ok(customerProfileDto);
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody CustomerUpdateDto dto) {
        customerService.updateCustomer(id, dto);
        return ResponseEntity.ok().body(dto);
    }
}
