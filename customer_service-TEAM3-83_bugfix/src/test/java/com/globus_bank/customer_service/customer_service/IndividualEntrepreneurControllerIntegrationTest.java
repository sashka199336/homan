package com.globus_bank.customer_service.customer_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globus_bank.customer_service.dto.response.IndividualEntrepreneurResponseOk;
import com.globus_bank.customer_service.dto.update.IndividualEntrepreneurUpdateDto;
import com.globus_bank.customer_service.entity.CustomerEntity;
import com.globus_bank.customer_service.entity.IndividualEntrepreneurEntity;
import com.globus_bank.customer_service.repository.CustomerRepository;
import com.globus_bank.customer_service.repository.IndividualEntrepreneurRepository;
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
public class IndividualEntrepreneurControllerIntegrationTest extends IntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private IndividualEntrepreneurRepository individualEntrepreneurRepository;

    private CustomerEntity customer;
    
    private IndividualEntrepreneurEntity individualEntrepreneur;
    
    @BeforeEach
    void setupData() {
        customer = customerRepository.save(TestDataUtils.createCustomer());
        individualEntrepreneur = TestDataUtils.createIndividualEntrepreneur();
        individualEntrepreneur.setCustomer(customer);
        individualEntrepreneurRepository.save(individualEntrepreneur);
    }
    
    @Test
    void whenGetIndividualEntrepreneurByCustomerId_thenReturnDto() throws Exception {
        mockMvc.perform(get("/api/v1/customers/{customerId}/individual-entrepreneurs", customer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    IndividualEntrepreneurResponseOk response = objectMapper.readValue(json, IndividualEntrepreneurResponseOk.class);
                    assertThat(response.getIndividualEntrepreneurId()).isEqualTo(individualEntrepreneur.getId());
                    assertThat(response.getOgrnip()).isEqualTo(individualEntrepreneur.getOgrnip());
                    assertThat(response.getCompanyName()).isEqualTo(individualEntrepreneur.getEntrepreneurName());
                });
    }
    
    @Test
    void whenGetIndividualEntrepreneurByNonExistingCustomerId_thenReturnNotFound() throws Exception {
        UUID nonExistingCustomerId = UUID.randomUUID();
        
        mockMvc.perform(get("/api/v1/customers/{customerId}/individual-entrepreneurs", nonExistingCustomerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void testSuccessfulIndividualEntrepreneurUpdate() throws Exception {
        IndividualEntrepreneurUpdateDto validDto = new IndividualEntrepreneurUpdateDto();
        validDto.setOgrnip("123456789012345");
        validDto.setEntrepreneurName("Иван Петров");
        
        ResultActions result = mockMvc.perform(
                        patch("/api/v1/customers/" + customer.getId() + "/individual-entrepreneurs/" + individualEntrepreneur.getId())
                                .content(objectMapper.writeValueAsString(validDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        result.andExpect(jsonPath("$.ogrnip").value("123456789012345"))
                .andExpect(jsonPath("$.entrepreneurName").value("Иван Петров"));
    }
    
    @Test
    public void testFailedIndividualEntrepreneurUpdate() throws Exception {
        IndividualEntrepreneurUpdateDto invalidDto = new IndividualEntrepreneurUpdateDto();
        invalidDto.setOgrnip("");
        invalidDto.setEntrepreneurName("");
        
        ResultActions result = mockMvc.perform(
                        patch("/api/v1/customers/" + customer.getId() + "/individual-entrepreneurs/" + individualEntrepreneur.getId())
                                .content(objectMapper.writeValueAsString(invalidDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        
        result.andExpect(jsonPath("$.error", Matchers.is("Validation Errors")))
                .andExpect(jsonPath("$.message", Matchers.containsString("ОГРНИП не должен быть пустым")))
                .andExpect(jsonPath("$.message", Matchers.containsString("Имя предпринимателя не должно быть пустым")));
    }
}
