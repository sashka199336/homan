package com.globus_bank.customer_service.service;

import com.globus_bank.customer_service.dto.common.ContactsDto;
import com.globus_bank.customer_service.dto.update.ContactsUpdateDto;
import com.globus_bank.customer_service.entity.ContactsEntity;
import com.globus_bank.customer_service.exception.ResourceNotFoundException;
import com.globus_bank.customer_service.repository.ContactRepository;
import com.globus_bank.customer_service.repository.CustomerRepository;
import com.globus_bank.customer_service.utils.mapper.dto.ContactMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {
    
    private final ContactRepository contactsRepository;
    
    private final CustomerRepository customerRepository;
    
    private final ContactMapper contactMapper;
    
    @Transactional(readOnly = true)
    public List<ContactsDto> findAllByCustomerId(UUID id) {
        customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент не найден"));
        
        return contactsRepository.findAllByCustomerId(id).stream()
                .map(contactMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void updateContacts(UUID id, ContactsUpdateDto dto) {
        ContactsEntity contacts = contactsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Контактные данные клиента не найдены"));
        
        contactMapper.updateFromRequest(dto, contacts);
        contactsRepository.save(contacts);
    }
    
    @Transactional
    public Optional<ContactsDto> findLatestUpdatedByCustomerId(UUID customerId) {
        return contactsRepository.findTopByCustomer_IdOrderByUpdatedAtDesc(customerId)
                .map(contactMapper::toDto);
    }
}
