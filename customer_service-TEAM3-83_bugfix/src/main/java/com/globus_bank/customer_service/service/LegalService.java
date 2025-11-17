package com.globus_bank.customer_service.service;

import com.globus_bank.customer_service.dto.common.LegalDto;
import com.globus_bank.customer_service.dto.response.LegalResponseOk;
import com.globus_bank.customer_service.dto.update.LegalEntityUpdateDto;
import com.globus_bank.customer_service.entity.LegalEntity;
import com.globus_bank.customer_service.exception.ResourceNotFoundException;
import com.globus_bank.customer_service.repository.CustomerRepository;
import com.globus_bank.customer_service.repository.LegalRepository;
import com.globus_bank.customer_service.utils.mapper.dto.LegalEntityMapper;
import com.globus_bank.customer_service.utils.mapper.response.LegalResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LegalService {
    
    private final LegalRepository legalEntityRepository;
    
    private final CustomerRepository customerRepository;
    
    private final LegalResponseMapper legalResponseMapper;
    
    private final LegalEntityMapper legalEntityMapper;
    
    @Transactional(readOnly = true)
    public LegalResponseOk findByCustomerId(UUID id) {
        customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент не найден"));
        
        return legalEntityRepository.findByCustomerId(id)
                .map(legalResponseMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Юридическое лицо не найдено"));
    }
    
    @Transactional
    public void updateLegalEntity(UUID id, LegalEntityUpdateDto dto) {
        LegalEntity legalEntity = legalEntityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Юридическое лицо не найдено"));
        
        legalEntityMapper.updateFromRequest(dto, legalEntity);
        legalEntityRepository.save(legalEntity);
    }
    
    @Transactional
    public Optional<LegalDto> findLatestUpdatedByCustomerId(UUID customerId) {
        return legalEntityRepository.findTopByCustomer_IdOrderByUpdatedAtDesc(customerId)
                .map(legalEntityMapper::toDto);
    }
}
