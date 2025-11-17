package com.globus_bank.customer_service.customer_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globus_bank.customer_service.dto.response.LegalResponseOk;
import com.globus_bank.customer_service.dto.update.LegalEntityUpdateDto;
import com.globus_bank.customer_service.entity.CustomerEntity;
import com.globus_bank.customer_service.entity.LegalEntity;
import com.globus_bank.customer_service.entity.enums.Position;
import com.globus_bank.customer_service.repository.CustomerRepository;
import com.globus_bank.customer_service.repository.LegalRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class LegalControllerIntegrationTest extends IntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LegalRepository legalRepository;

    private CustomerEntity customer;
    
    private LegalEntity legalEntity;
    
    @BeforeEach
    void setupData() {
        customer = customerRepository.save(TestDataUtils.createCustomer());
        legalEntity = TestDataUtils.createLegalEntity();
        legalEntity.setCustomer(customer);
        legalEntity = legalRepository.save(legalEntity);
    }
    
    @Test
    void whenGetLegalEntityByCustomerId_thenReturnDto() throws Exception {
        mockMvc.perform(get("/api/v1/customers/{customerId}/legal-entities", customer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    LegalResponseOk response = objectMapper.readValue(json, LegalResponseOk.class);
                    assertThat(response.getLegalEntityId()).isEqualTo(legalEntity.getId());
                    assertThat(response.getCompanyName()).isEqualTo(legalEntity.getCompanyName());
                    assertThat(response.getOgrn()).isEqualTo(legalEntity.getOgrn());
                    assertThat(response.getPosition()).isEqualTo(legalEntity.getPosition().toString());
                });
    }
    
    @Test
    void whenGetLegalEntityByNonExistingCustomerId_thenReturnNotFound() throws Exception {
        UUID nonExistingCustomerId = UUID.randomUUID();
        
        mockMvc.perform(get("/api/v1/customers/{customerId}/legal-entities", nonExistingCustomerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void testSuccessfulLegalEntityUpdate() throws Exception {
        LegalEntityUpdateDto validDto = new LegalEntityUpdateDto();
        validDto.setKpp("123456789");
        validDto.setOgrn("1234567890123");
        validDto.setCompanyName("ООО Рога и копыта");
        validDto.setPosition(Position.CEO);
        
        ResultActions result = mockMvc.perform(
                        patch("/api/v1/customers/" + customer.getId() + "/legal-entities/" + legalEntity.getId())
                                .content(objectMapper.writeValueAsString(validDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        result.andExpect(jsonPath("$.kpp").value("123456789"))
                .andExpect(jsonPath("$.ogrn").value("1234567890123"))
                .andExpect(jsonPath("$.companyName").value("ООО Рога и копыта"))
                .andExpect(jsonPath("$.position").value("CEO"));
    }
    
    @Test
    public void testFailedLegalEntityUpdate() throws Exception {
        LegalEntityUpdateDto invalidDto = new LegalEntityUpdateDto();
        invalidDto.setKpp("");
        invalidDto.setOgrn("");
        invalidDto.setCompanyName("");
        invalidDto.setPosition(null);
        
        ResultActions result = mockMvc.perform(
                        patch("/api/v1/customers/" + customer.getId() + "/legal-entities/" + legalEntity.getId())
                                .content(objectMapper.writeValueAsString(invalidDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        
        result.andExpect(jsonPath("$.error", Matchers.is("Validation Errors")))
                .andExpect(jsonPath("$.message", Matchers.containsString("КПП не должен быть пустым")))
                .andExpect(jsonPath("$.message", Matchers.containsString("ОГРН не должен быть пустым")))
                .andExpect(jsonPath("$.message", Matchers.containsString("Название компании не должно быть пустым")));
    }
}
