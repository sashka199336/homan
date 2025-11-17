package com.globus_bank.customer_service.customer_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globus_bank.customer_service.dto.response.CustomerResponseOk;
import com.globus_bank.customer_service.dto.update.CustomerUpdateDto;
import com.globus_bank.customer_service.entity.CustomerEntity;
import com.globus_bank.customer_service.entity.enums.CustomerType;
import com.globus_bank.customer_service.entity.enums.Status;
import com.globus_bank.customer_service.repository.CustomerRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class CustomerControllerIntegrationTest extends IntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private CustomerRepository customerRepository;

    private CustomerEntity customer;
    
    @BeforeEach
    void setupData() {
        customer = customerRepository.save(TestDataUtils.createCustomer());
    }
    
    @Test
    void whenGetCustomerById_thenReturnCustomer() throws Exception {
        mockMvc.perform(get("/api/v1/customers/{customerId}", customer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    CustomerResponseOk response = objectMapper.readValue(json, CustomerResponseOk.class);
                    assertThat(response.getCustomerId()).isEqualTo(customer.getId());
                    assertThat(response.getInn()).isEqualTo(customer.getInn());
                    assertThat(response.getCustomerType()).isEqualTo(customer.getCustomerType().toString());
                    assertThat(response.getStatus()).isEqualTo(customer.getStatusType().toString());
                });
    }
    
    @Test
    void whenGetCustomerByNonExistingId_thenReturnNotFound() throws Exception {
        UUID nonExistingCustomerId = UUID.randomUUID();
        
        mockMvc.perform(get("/api/v1/customers/{customerId}", nonExistingCustomerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void testSuccessfulCustomerUpdate() throws Exception {
        CustomerUpdateDto validDto = new CustomerUpdateDto();
        validDto.setInn("1234567890");
        validDto.setCustomerType(CustomerType.LEGAL_ENTITY);
        validDto.setStatusType(Status.ACTIVE);
        
        ResultActions result = mockMvc.perform(
                        patch("/api/v1/customers/" + customer.getId())
                                .content(objectMapper.writeValueAsString(validDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        result.andExpect(jsonPath("$.inn").value("1234567890"))
                .andExpect(jsonPath("$.customerType").value("LEGAL_ENTITY"))
                .andExpect(jsonPath("$.statusType").value("ACTIVE"));
    }
    
    @Test
    public void testFailedCustomerUpdate() throws Exception {
        CustomerUpdateDto invalidDto = new CustomerUpdateDto();
        invalidDto.setInn("");
        invalidDto.setCustomerType(null);
        invalidDto.setStatusType(null);
        
        ResultActions result = mockMvc.perform(
                        patch("/api/v1/customers/" + customer.getId())
                                .content(objectMapper.writeValueAsString(invalidDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        
        result.andExpect(jsonPath("$.error", Matchers.is("Validation Errors")))
                .andExpect(jsonPath("$.message", Matchers.containsString("ИНН должен содержать 10 или 12 цифр")))
                .andExpect(jsonPath("$.message", Matchers.containsString("Тип клиента не должен быть пустым")))
                .andExpect(jsonPath("$.message", Matchers.containsString("Статус клиента не должен быть пустым")));
    }
}
