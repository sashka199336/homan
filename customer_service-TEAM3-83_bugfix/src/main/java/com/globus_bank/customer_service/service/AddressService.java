package com.globus_bank.customer_service.service;

import com.globus_bank.customer_service.dto.common.AddressDto;
import com.globus_bank.customer_service.dto.response.AddressResponseOk;
import com.globus_bank.customer_service.dto.update.AddressUpdateDto;
import com.globus_bank.customer_service.entity.AddressEntity;
import com.globus_bank.customer_service.exception.ResourceNotFoundException;
import com.globus_bank.customer_service.repository.AddressRepository;
import com.globus_bank.customer_service.repository.CustomerRepository;
import com.globus_bank.customer_service.utils.mapper.dto.AddressMapper;
import com.globus_bank.customer_service.utils.mapper.response.AddressResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {
    
    private final AddressRepository addressRepository;
    
    private final CustomerRepository customerRepository;
    
    private final AddressResponseMapper addressResponseMapper;
    
    private final AddressMapper addressMapper;
    
    @Transactional(readOnly = true)
    public List<AddressResponseOk> findAllByCustomerId(UUID id) {
        customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент не найден"));
        
        return addressRepository.findAllByCustomerId(id).stream()
                .map(addressResponseMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void updateAddress(UUID id, AddressUpdateDto dto) {
        AddressEntity existingAddress = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Адрес не найден"));
        
        addressMapper.updateFromRequest(dto, existingAddress);
        addressRepository.save(existingAddress);
    }
    
    @Transactional
    public Optional<AddressDto> findLatestUpdatedByCustomerId(UUID customerId) {
        return addressRepository.findTopByCustomer_IdOrderByUpdatedAtDesc(customerId)
                .map(addressMapper::toDto);
    }
}
