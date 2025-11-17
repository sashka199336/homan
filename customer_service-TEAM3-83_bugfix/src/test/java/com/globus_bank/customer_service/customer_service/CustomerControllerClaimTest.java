package com.globus_bank.customer_service.customer_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globus_bank.customer_service.dto.response.CustomerProfileDto;
import com.globus_bank.customer_service.entity.AddressEntity;
import com.globus_bank.customer_service.entity.ContactsEntity;
import com.globus_bank.customer_service.entity.CustomerEntity;
import com.globus_bank.customer_service.entity.IndividualEntrepreneurEntity;
import com.globus_bank.customer_service.repository.AddressRepository;
import com.globus_bank.customer_service.repository.ContactRepository;
import com.globus_bank.customer_service.repository.CustomerRepository;
import com.globus_bank.customer_service.repository.IndividualEntrepreneurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class CustomerControllerClaimTest extends IntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    private ContactRepository contactRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private IndividualEntrepreneurRepository entrepreneurRepository;
    
    private AddressEntity address;
    
    private ContactsEntity contact;
    
    private CustomerEntity customer;
    
    private IndividualEntrepreneurEntity entrepreneur;
    
    @BeforeEach
    void setupData() {
        customer = TestDataUtils.createCustomer();
        address = TestDataUtils.createAddress();
        address.setCustomer(customer);
        contact = TestDataUtils.createContact();
        contact.setCustomer(customer);
        entrepreneur = TestDataUtils.createIndividualEntrepreneur();
        entrepreneur.setCustomer(customer);
        customerRepository.save(customer);
        addressRepository.save(address);
        contactRepository.save(contact);
        entrepreneurRepository.save(entrepreneur);
    }
    
    @Test
    void whenGetCustomerProfileById_thenReturnCustomerProfile() throws Exception {
        CustomerProfileDto dto = getCustomerProfileDto();
        
        mockMvc.perform(get("/api/v1/customers/customer-profile/" + customer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    CustomerProfileDto actualProfile = objectMapper.readValue(json, CustomerProfileDto.class);
                    
                    assertThat(actualProfile.getCountry()).isEqualTo(dto.getCountry());
                    assertThat(actualProfile.getCity()).isEqualTo(dto.getCity());
                    assertThat(actualProfile.getStreet()).isEqualTo(dto.getStreet());
                    assertThat(actualProfile.getHouseNumber()).isEqualTo(dto.getHouseNumber());
                    assertThat(actualProfile.getApartmentNumber()).isEqualTo(dto.getApartmentNumber());
                    assertThat(actualProfile.getPostalCode()).isEqualTo(dto.getPostalCode());
                    assertThat(actualProfile.getAddressType()).isEqualTo(dto.getAddressType());
                    assertThat(actualProfile.getPhoneNumber()).isEqualTo(dto.getPhoneNumber());
                    assertThat(actualProfile.getMail()).isEqualTo(dto.getMail());
                    assertThat(actualProfile.getInn()).isEqualTo(dto.getInn());
                    assertThat(actualProfile.getCustomerStatus()).isEqualTo(dto.getCustomerStatus());
                    assertThat(actualProfile.getOgrnip()).isEqualTo(dto.getOgrnip());
                    assertThat(actualProfile.getEntrepreneurName()).isEqualTo(dto.getEntrepreneurName());
                });
    }
    
    @Test
    void whenGetCustomerProfileById_thenReturnCustomerProfileWithDifferentAddress() throws Exception {
        CustomerProfileDto dto = getCustomerProfileDto();
        dto.setStreet("ошибка в наименовании улицы");
        
        mockMvc.perform(get("/api/v1/customers/customer-profile/" + customer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    String json = result.getResponse().getContentAsString();
                    CustomerProfileDto actualProfile = objectMapper.readValue(json, CustomerProfileDto.class);
                    
                    assertThat(actualProfile.getCountry()).isEqualTo(dto.getCountry());
                    assertThat(actualProfile.getCity()).isEqualTo(dto.getCity());
                    assertThat(actualProfile.getHouseNumber()).isEqualTo(dto.getHouseNumber());
                    assertThat(actualProfile.getApartmentNumber()).isEqualTo(dto.getApartmentNumber());
                    assertThat(actualProfile.getPostalCode()).isEqualTo(dto.getPostalCode());
                    assertThat(actualProfile.getAddressType()).isEqualTo(dto.getAddressType());
                    assertThat(actualProfile.getPhoneNumber()).isEqualTo(dto.getPhoneNumber());
                    assertThat(actualProfile.getMail()).isEqualTo(dto.getMail());
                    assertThat(actualProfile.getInn()).isEqualTo(dto.getInn());
                    assertThat(actualProfile.getCustomerStatus()).isEqualTo(dto.getCustomerStatus());
                    assertThat(actualProfile.getOgrnip()).isEqualTo(dto.getOgrnip());
                    assertThat(actualProfile.getEntrepreneurName()).isEqualTo(dto.getEntrepreneurName());
                    assertThat(actualProfile.getStreet()).isNotEqualTo(dto.getStreet());
                });
    }
    
    private CustomerProfileDto getCustomerProfileDto() {
        CustomerProfileDto dto = new CustomerProfileDto();
        
        dto.setCountry(address.getCountry());
        dto.setCity(address.getCity());
        dto.setStreet(address.getStreet());
        dto.setHouseNumber(address.getHouseNumber());
        dto.setApartmentNumber(address.getApartmentNumber());
        dto.setPostalCode(address.getPostalCode());
        dto.setAddressType(String.valueOf(address.getAddressType()));
        dto.setPhoneNumber(contact.getPhoneNumber());
        dto.setMail(contact.getEmail());
        dto.setInn(customer.getInn());
        dto.setCustomerStatus(String.valueOf(customer.getStatusType()));
        dto.setOgrnip(entrepreneur.getOgrnip());
        dto.setEntrepreneurName(entrepreneur.getEntrepreneurName());
        
        return dto;
    }
}
