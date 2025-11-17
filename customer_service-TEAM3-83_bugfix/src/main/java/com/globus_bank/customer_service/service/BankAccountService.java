package com.globus_bank.customer_service.service;

import com.globus_bank.customer_service.dto.response.BankAccountResponseOk;
import com.globus_bank.customer_service.dto.update.BankAccountUpdateDto;
import com.globus_bank.customer_service.entity.BankAccountEntity;
import com.globus_bank.customer_service.exception.ResourceNotFoundException;
import com.globus_bank.customer_service.repository.BankAccountRepository;
import com.globus_bank.customer_service.repository.CustomerRepository;
import com.globus_bank.customer_service.utils.mapper.dto.BankAccountMapper;
import com.globus_bank.customer_service.utils.mapper.response.BankAccountResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankAccountService {
    
    private final BankAccountRepository bankAccountRepository;
    
    private final CustomerRepository customerRepository;
    
    private final BankAccountResponseMapper bankAccountResponseMapper;
    
    private final BankAccountMapper bankAccountMapper;
    
    @Transactional(readOnly = true)
    public List<BankAccountResponseOk> findAllByCustomerId(UUID id) {
        customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент не найден"));
        
        return bankAccountRepository.findAllByCustomerId(id).stream()
                .map(bankAccountResponseMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void updateBankAccount(UUID id, BankAccountUpdateDto dto) {
        BankAccountEntity bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Банковские реквизиты не найдены"));
        
        bankAccountMapper.updateFromRequest(dto, bankAccount);
        bankAccountRepository.save(bankAccount);
    }
}
