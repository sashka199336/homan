package com.globus_bank.customer_service.service;

import com.globus_bank.customer_service.dto.response.DocumentTypeResponseOk;
import com.globus_bank.customer_service.dto.update.DocumentsTypeUpdateDto;
import com.globus_bank.customer_service.entity.DocumentsTypesEntity;
import com.globus_bank.customer_service.exception.DocumentCustomerMismatchException;
import com.globus_bank.customer_service.exception.ResourceNotFoundException;
import com.globus_bank.customer_service.repository.CustomerRepository;
import com.globus_bank.customer_service.repository.DocumentsTypesRepository;
import com.globus_bank.customer_service.utils.mapper.dto.DocumentTypeMapper;
import com.globus_bank.customer_service.utils.mapper.response.DocumentTypeResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentsTypesService {
    
    private final DocumentsTypesRepository documentsTypesRepository;
    
    private final CustomerRepository customerRepository;
    
    private final DocumentTypeResponseMapper documentTypeResponseMapper;
    
    private final DocumentTypeMapper documentTypeMapper;
    
    @Transactional(readOnly = true)
    public List<DocumentTypeResponseOk> findAllByCustomerId(UUID id) {
        customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент не найден"));
        
        return documentsTypesRepository.findAllByCustomerId(id).stream()
                .map(documentTypeResponseMapper::toResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public byte[] getDocumentScanUrl(UUID customerId, UUID documentId) {
        DocumentsTypesEntity document = documentsTypesRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Документ не найден"));
        
        if(!document.getCustomer().getId().equals(customerId)) {
            throw new DocumentCustomerMismatchException("Документ не принадлежит клиенту");
        }
        
        byte[] scan = document.getDocumentScan();
        if(scan == null || scan.length == 0) {
            throw new ResourceNotFoundException("Документ не найден");
        }
        
        return scan;
    }
    
    @Transactional
    public void updateDocumentsType(UUID id, DocumentsTypeUpdateDto dto) {
        DocumentsTypesEntity documentsTypes = documentsTypesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Документ не найден"));
        
        documentTypeMapper.updateFromRequest(dto, documentsTypes);
        documentsTypesRepository.save(documentsTypes);
    }
}
