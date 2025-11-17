package com.globus_bank.customer_service.service;

import com.globus_bank.customer_service.dto.response.DocumentTypeResponseOk;
import com.globus_bank.customer_service.dto.response.PassportResponseOk;
import com.globus_bank.customer_service.dto.update.PassportUpdateDto;
import com.globus_bank.customer_service.entity.PassportEntity;
import com.globus_bank.customer_service.exception.DocumentCustomerMismatchException;
import com.globus_bank.customer_service.exception.ResourceNotFoundException;
import com.globus_bank.customer_service.repository.PassportRepository;
import com.globus_bank.customer_service.utils.mapper.dto.PassportMapper;
import com.globus_bank.customer_service.utils.mapper.response.PassportResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PassportService {
    
    private final PassportRepository passportRepository;
    
    private final PassportResponseMapper passportResponseMapper;
    
    private final PassportMapper passportMapper;
    
    private final DocumentsTypesService documentsTypesService;
    
    @Transactional(readOnly = true)
    public PassportResponseOk findByDocumentId(UUID documentId, UUID customerId) {
        PassportEntity entity = passportRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Паспорт не найден"));
        
        if(!entity.getDocument().getCustomer().getId().equals(customerId)) {
            throw new DocumentCustomerMismatchException("Документ не принадлежит клиенту");
        }
        
        return passportResponseMapper.toResponse(entity);
    }
    
    @Transactional
    public void updatePassport(UUID id, PassportUpdateDto dto) {
        PassportEntity passport = passportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Паспорт не найден"));
        
        passportMapper.updateFromRequest(dto, passport);
        passportRepository.save(passport);
    }
    
    @Transactional
    public Optional<PassportResponseOk> findPassportByCustomerId(UUID customerId) {
        List<DocumentTypeResponseOk> documents = documentsTypesService.findAllByCustomerId(customerId);
        
        return documents.stream()
                .map(DocumentTypeResponseOk::getDocumentId)
                .map(documentId -> findByDocumentId(documentId, customerId))
                .findFirst();
    }
}
