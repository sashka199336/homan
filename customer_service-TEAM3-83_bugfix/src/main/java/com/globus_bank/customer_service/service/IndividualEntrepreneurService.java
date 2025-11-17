package com.globus_bank.customer_service.service;

import com.globus_bank.customer_service.dto.common.IndividualEntrepreneurDto;
import com.globus_bank.customer_service.dto.response.IndividualEntrepreneurResponseOk;
import com.globus_bank.customer_service.dto.update.IndividualEntrepreneurUpdateDto;
import com.globus_bank.customer_service.entity.IndividualEntrepreneurEntity;
import com.globus_bank.customer_service.exception.ResourceNotFoundException;
import com.globus_bank.customer_service.repository.CustomerRepository;
import com.globus_bank.customer_service.repository.IndividualEntrepreneurRepository;
import com.globus_bank.customer_service.utils.mapper.dto.IndividualEntrepreneurMapper;
import com.globus_bank.customer_service.utils.mapper.response.IndividualEntrepreneurResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IndividualEntrepreneurService {
    
    private final IndividualEntrepreneurRepository individualEntrepreneurRepository;
    
    private final CustomerRepository customerRepository;
    
    private final IndividualEntrepreneurResponseMapper individualEntrepreneurResponseMapper;
    
    private final IndividualEntrepreneurMapper individualEntrepreneurMapper;
    
    @Transactional(readOnly = true)
    public IndividualEntrepreneurResponseOk findByCustomerId(UUID id) {
        customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент не найден"));
        
        return individualEntrepreneurRepository.findByCustomerId(id)
                .map(individualEntrepreneurResponseMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Индивидуальный предприниматель не найден"));
    }
    
    @Transactional
    public void updateIndividualEntrepreneur(UUID id, IndividualEntrepreneurUpdateDto dto) {
        IndividualEntrepreneurEntity individualEntrepreneur = individualEntrepreneurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ИП не найден"));
        
        individualEntrepreneurMapper.updateFromRequest(dto, individualEntrepreneur);
        individualEntrepreneurRepository.save(individualEntrepreneur);
    }
    
    @Transactional
    public Optional<IndividualEntrepreneurDto> findLatestUpdatedByCustomerId(UUID customerId) {
        return individualEntrepreneurRepository.findTopByCustomer_IdOrderByUpdatedAtDesc(customerId)
                .map(individualEntrepreneurMapper::toDto);
    }
}
