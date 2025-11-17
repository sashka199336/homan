package com.globus_bank.customer_service.service;

import com.globus_bank.customer_service.dto.common.*;
import com.globus_bank.customer_service.dto.response.CustomerProfileDto;
import com.globus_bank.customer_service.dto.response.CustomerResponseOk;
import com.globus_bank.customer_service.dto.response.PassportResponseOk;
import com.globus_bank.customer_service.dto.update.CustomerUpdateDto;
import com.globus_bank.customer_service.entity.*;
import com.globus_bank.customer_service.exception.BankDoesNotWorkWithIndividualsException;
import com.globus_bank.customer_service.exception.ClientWithTinAlreadyExistsException;
import com.globus_bank.customer_service.exception.InvalidClientTypeException;
import com.globus_bank.customer_service.exception.ResourceNotFoundException;
import com.globus_bank.customer_service.repository.*;
import com.globus_bank.customer_service.utils.mapper.dto.*;
import com.globus_bank.customer_service.kafka.NotificationRuleProducer;
import com.globus_bank.customer_service.utils.mapper.response.CustomerResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {
    
    private final AddressMapper addressMapper;
    
    private final AddressRepository addressRepository;
    
    private final AddressService addressService;
    
    private final BankAccountMapper bankAccountMapper;
    
    private final BankAccountRepository bankAccountRepository;
    
    private final ContactMapper contactMapper;
    
    private final ContactRepository contactRepository;
    
    private final ContactService contactService;
    
    private final CustomerMapper customerMapper;
    
    private final CustomerProfileMapper customerProfileMapper;
    
    private final CustomerRepository customerRepository;
    
    private final CustomerResponseMapper customerResponseMapper;
    
    private final DocumentTypeMapper documentTypeMapper;
    
    private final DocumentsTypesRepository documentsTypesRepository;
    
    private final IndividualEntrepreneurMapper individualEntrepreneurMapper;
    
    private final IndividualEntrepreneurRepository individualEntrepreneurRepository;
    
    private final IndividualEntrepreneurService individualEntrepreneurService;
    
    private final LegalEntityMapper legalEntityMapper;
    
    private final LegalRepository legalRepository;
    
    private final LegalService legalService;
    
    private final PassportMapper passportMapper;
    
    private final PassportRepository passportRepository;

    private final NotificationRuleProducer notificationRuleProducer;
    
    private final PassportService passportService;
    
    @Transactional
    public CustomerEntity saveFullCustomer(FullCustomerDto fullCustomerDto) {
        validateCustomerData(fullCustomerDto);
        
        CustomerEntity customerEntity = customerMapper.toEntity(fullCustomerDto.getCustomer());
        CustomerEntity savedCustomer = customerRepository.save(customerEntity);
        
        List<AddressEntity> addresses = fullCustomerDto.getAddresses().stream()
                .map(address -> {
                    AddressEntity entity = addressMapper.toEntity(address);
                    entity.setCustomer(savedCustomer);
                    return entity;
                }).toList();
        addressRepository.saveAll(addresses);
        
        List<ContactsEntity> contacts = fullCustomerDto.getContacts().stream()
                .map(contact -> {
                    ContactsEntity entity = contactMapper.toEntity(contact);
                    entity.setCustomer(savedCustomer);
                    return entity;
                }).toList();
        contactRepository.saveAll(contacts);
        
        PassportEntity passportEntity = passportMapper.toEntity(fullCustomerDto.getPassport());
        PassportEntity savedPassport = passportRepository.save(passportEntity);
        
        List<DocumentsTypesEntity> documents = fullCustomerDto.getDocuments().stream()
                .map(documentDto -> {
                    DocumentsTypesEntity entity = documentTypeMapper.toEntity(documentDto);
                    entity.setCustomer(savedCustomer);
                    entity.setPassport(savedPassport);
                    return entity;
                })
                .toList();
        documentsTypesRepository.saveAll(documents);
        
        Optional.ofNullable(fullCustomerDto.getBankAccounts())
                .ifPresent(bankAccounts -> {
            List<BankAccountEntity> entities = bankAccounts.stream()
                    .map(account -> {
                        BankAccountEntity entity = bankAccountMapper.toEntity(account);
                        entity.setCustomer(savedCustomer);
                        return entity;
                    })
                    .toList();
            bankAccountRepository.saveAll(entities);
        });
        
        Optional.ofNullable(fullCustomerDto.getIndividualEntrepreneur())
                .ifPresent(ipe -> {
                    IndividualEntrepreneurEntity ipeEntity = individualEntrepreneurMapper.toEntity(ipe);
                    ipeEntity.setCustomer(savedCustomer);
                    individualEntrepreneurRepository.save(ipeEntity);
                    savedCustomer.setIndividualEntrepreneurEntity(ipeEntity);
                });
        
        Optional.ofNullable(fullCustomerDto.getLegalEntity())
                .ifPresent(le -> {
                    LegalEntity legalEntity = legalEntityMapper.toEntity(le);
                    legalEntity.setCustomer(savedCustomer);
                    legalRepository.save(legalEntity);
                    savedCustomer.setLegalEntity(legalEntity);
                });

        fullCustomerDto.getContacts()
                .forEach(c -> c.setCustomerId(customerEntity.getId()));
        notificationRuleProducer.sendNotificationRule(fullCustomerDto.getContacts());

        return customerEntity;
    }
    
    @Transactional(readOnly = true)
    public CustomerResponseOk findById(UUID id) {
        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент не найден"));
        
        return customerResponseMapper.toResponse(customer);
    }
    
    @Transactional
    public void updateCustomer(UUID id, CustomerUpdateDto dto) {
        CustomerEntity existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент не найден"));

        customerMapper.updateFromRequest(dto, existingCustomer);
        customerRepository.save(existingCustomer);
    }
    
    @Transactional
    public CustomerProfileDto getCustomerProfile(UUID id) {
        customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент не найден"));
        
        Optional<AddressDto> address = addressService.findLatestUpdatedByCustomerId(id);
        Optional<ContactsDto> contact = contactService.findLatestUpdatedByCustomerId(id);
        CustomerResponseOk customer = findById(id);
        Optional<IndividualEntrepreneurDto> individualEntrepreneurDto =
                individualEntrepreneurService.findLatestUpdatedByCustomerId(id);
        Optional<LegalDto> legal = legalService.findLatestUpdatedByCustomerId(id);
        Optional<PassportResponseOk> passport = passportService.findPassportByCustomerId(id);
        
        return customerProfileMapper
                .toCustomerProfileDto(address.orElse(null),
                        contact.orElse(null),
                        customer, individualEntrepreneurDto.orElse(null),
                        legal.orElse(null), passport.orElse(null));
    }
    
    private void validateCustomerData(FullCustomerDto dto) {
        if(customerRepository.existsByInn(dto.getCustomer().getInn())) {
            throw new ClientWithTinAlreadyExistsException("Клиент с таким ИНН уже существует");
        }
        
        if(dto.getLegalEntity() != null && dto.getIndividualEntrepreneur() != null) {
            throw new InvalidClientTypeException("Клиент должен быть либо ЮЛ, либо ИП");
        }
        
        if(dto.getLegalEntity() == null && dto.getIndividualEntrepreneur() == null) {
            throw new BankDoesNotWorkWithIndividualsException("Банк не обслуживает физических лиц");
        }
    }
}
