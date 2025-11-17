package com.globus_bank.customer_service.customer_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globus_bank.customer_service.dto.response.PassportResponseOk;
import com.globus_bank.customer_service.dto.update.PassportUpdateDto;
import com.globus_bank.customer_service.entity.CustomerEntity;
import com.globus_bank.customer_service.entity.DocumentsTypesEntity;
import com.globus_bank.customer_service.entity.PassportEntity;
import com.globus_bank.customer_service.repository.CustomerRepository;
import com.globus_bank.customer_service.repository.DocumentsTypesRepository;
import com.globus_bank.customer_service.repository.PassportRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class PassportControllerIntegrationTest extends IntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PassportRepository passportRepository;

    @Autowired
    private DocumentsTypesRepository documentsTypesRepository;

    private CustomerEntity customer;
    
    private DocumentsTypesEntity document;
    
    private PassportEntity passport;
    
    @BeforeEach
    void setupData() {
        customer = customerRepository.save(TestDataUtils.createCustomer());
        passport = passportRepository.save(TestDataUtils.createPassport());
        document = TestDataUtils.createDocumentType();
        document.setPassport(passport);
        document.setCustomer(customer);
        document = documentsTypesRepository.save(document);
        passport.setDocument(document);
    }

    @Test
    void whenGetPassportByDocumentAndCustomer_thenReturnPassportResponse() throws Exception {
        mockMvc.perform(get("/api/v1/customers/{customerId}/documents-types/{documentId}/passport",
                        customer.getId(), document.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    PassportResponseOk response = objectMapper.readValue(json, PassportResponseOk.class);
                    assertThat(response.getSeries()).isEqualTo(passport.getSeries());
                    assertThat(response.getNumber()).isEqualTo(passport.getNumber());
                    assertThat(response.getIssuedBy()).isEqualTo(passport.getIssuedBy());
                    assertThat(response.getDateOfBirth()).isEqualTo(passport.getDateOfBirth());
                    assertThat(response.getIssueDate()).isEqualTo(passport.getIssueDate());
                    assertThat(response.getFirstName()).isEqualTo(passport.getFirstName());
                    assertThat(response.getLastName()).isEqualTo(passport.getLastName());
                    assertThat(response.getPatronymic()).isEqualTo(passport.getPatronymic());
                    assertThat(response.getIssueCode()).isEqualTo(passport.getIssueCode());
                });
    }
    
    @Test
    void whenGetPassportByNonExistingDocument_thenReturnNotFound() throws Exception {
        UUID nonExistingDocumentId = UUID.randomUUID();
        
        mockMvc.perform(get("/api/v1/customers/{customerId}/documents-types/{documentId}/passport",
                        customer.getId(), nonExistingDocumentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void testSuccessfulPassportUpdate() throws Exception {
        PassportUpdateDto validDto = new PassportUpdateDto();
        validDto.setFirstName("Иван");
        validDto.setLastName("Петров");
        validDto.setPatronymic("Иванович");
        validDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        validDto.setSeries("1234");
        validDto.setNumber("567890");
        validDto.setIssueDate(LocalDate.of(2023, 1, 1));
        validDto.setIssuedBy("Отдел УФМС");
        validDto.setIssueCode("123-456");
        
        ResultActions result = mockMvc.perform(
                        patch("/api/v1/customers/" + customer.getId() + "/documents-types/" +
                                document.getId() + "/passport/" + passport.getId())
                                .content(objectMapper.writeValueAsString(validDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        result.andExpect(jsonPath("$.firstName").value("Иван"))
                .andExpect(jsonPath("$.lastName").value("Петров"))
                .andExpect(jsonPath("$.dateOfBirth").value("1990-01-01"))
                .andExpect(jsonPath("$.series").value("1234"))
                .andExpect(jsonPath("$.number").value("567890"))
                .andExpect(jsonPath("$.issueDate").value("2023-01-01"))
                .andExpect(jsonPath("$.issuedBy").value("Отдел УФМС"))
                .andExpect(jsonPath("$.issueCode").value("123-456"));
    }
    
    @Test
    public void testFailedPassportUpdate() throws Exception {
        PassportUpdateDto invalidDto = new PassportUpdateDto();
        invalidDto.setFirstName("");
        invalidDto.setLastName("");
        invalidDto.setDateOfBirth(null);
        invalidDto.setSeries("");
        invalidDto.setNumber("");
        invalidDto.setIssueDate(null);
        invalidDto.setIssuedBy("");
        invalidDto.setIssueCode("");
        
        ResultActions result = mockMvc.perform(
                        patch("/api/v1/customers/" + customer.getId() + "/documents-types/" +
                                document.getId() + "/passport/" + passport.getId())
                                .content(objectMapper.writeValueAsString(invalidDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        
        result.andExpect(jsonPath("$.error", Matchers.is("Validation Errors")))
                .andExpect(jsonPath("$.message", Matchers.containsString("Имя не должно быть пустым")))
                .andExpect(jsonPath("$.message", Matchers.containsString("Фамилия не должна быть пустой")))
                .andExpect(jsonPath("$.message", Matchers.containsString("Дата рождения не должна быть пустой")))
                .andExpect(jsonPath("$.message", Matchers.containsString("Серия не должна быть пустой")))
                .andExpect(jsonPath("$.message", Matchers.containsString("Номер не должен быть пустым")))
                .andExpect(jsonPath("$.message", Matchers.containsString("Дата выдачи не должна быть пустой")))
                .andExpect(jsonPath("$.message", Matchers.containsString("Кем выдано не должно быть пустым")))
                .andExpect(jsonPath("$.message", Matchers.containsString("Код подразделения не должен быть пустым")));
    }
}
