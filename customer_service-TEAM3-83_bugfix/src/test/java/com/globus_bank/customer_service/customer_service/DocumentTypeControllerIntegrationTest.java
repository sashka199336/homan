package com.globus_bank.customer_service.customer_service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globus_bank.customer_service.dto.response.DocumentTypeResponseOk;
import com.globus_bank.customer_service.dto.update.DocumentsTypeUpdateDto;
import com.globus_bank.customer_service.entity.CustomerEntity;
import com.globus_bank.customer_service.entity.DocumentsTypesEntity;
import com.globus_bank.customer_service.repository.CustomerRepository;
import com.globus_bank.customer_service.repository.DocumentsTypesRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class DocumentTypeControllerIntegrationTest extends IntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DocumentsTypesRepository documentsTypesRepository;

    private CustomerEntity customer;
    
    private DocumentsTypesEntity documentType1;
    
    private DocumentsTypesEntity documentType2;
    
    @BeforeEach
    void setupData() {
        customer = customerRepository.save(TestDataUtils.createCustomer());

        documentType1 = TestDataUtils.createDocumentType();
        documentType1.setCustomer(customer);
        documentType1 = documentsTypesRepository.save(documentType1);

        documentType2 = TestDataUtils.createDocumentType();
        documentType2.setCustomer(customer);
        documentType2 = documentsTypesRepository.save(documentType2);
    }
    
    @Test
    void whenGetDocumentTypesByCustomerId_thenReturnDocumentTypes() throws Exception {
        mockMvc.perform(get("/api/v1/customers/{customerId}/documents-types", customer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    List<DocumentTypeResponseOk> documentTypes = objectMapper.readValue(json, new TypeReference<>() {
                    });
                    assertThat(documentTypes).hasSize(2);
                    assertThat(documentTypes).extracting("documentType")
                            .containsExactlyInAnyOrder(documentType1.getAdditionalInformation().get("type"), documentType2.getAdditionalInformation().get("type"));
                    assertThat(documentTypes).extracting("documentDate").containsExactlyInAnyOrder(documentType1.getDocumentDate(),
                            documentType2.getDocumentDate());
                });
    }
    
    @Test
    void whenGetDocumentScan_thenReturnScanBytes() throws Exception {
        mockMvc.perform(get("/api/v1/customers/{customerId}/documents-types/{documentId}/scan",
                        customer.getId(), documentType1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    byte[] content = result.getResponse().getContentAsByteArray();
                    assertThat(content).isEqualTo(documentType1.getDocumentScan());
                });
    }
    
    @Test
    void whenGetDocumentTypesByNonExistingCustomer_thenReturnNotFound() throws Exception {
        UUID nonExistingCustomerId = UUID.randomUUID();
        
        mockMvc.perform(get("/api/v1/customers/{customerId}/documents-types", nonExistingCustomerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void whenGetDocumentScanWithInvalidIds_thenReturnNotFound() throws Exception {
        UUID nonExistingCustomerId = UUID.randomUUID();
        UUID nonExistingDocumentId = UUID.randomUUID();
        
        mockMvc.perform(get("/api/v1/customers/{customerId}/documents-types/{documentId}/scan",
                        nonExistingCustomerId, nonExistingDocumentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void testSuccessfulDocumentUpdate() throws Exception {
        DocumentsTypeUpdateDto validDto = new DocumentsTypeUpdateDto();
        validDto.setDocumentDate(LocalDate.of(2025, 1, 1));
        validDto.setDocumentScan("some-byte-array".getBytes());
        validDto.setAdditionalInformation(Map.of("key", "value"));
        
        ResultActions result = mockMvc.perform(
                        patch("/api/v1/customers/" + customer.getId() + "/documents-types/" + documentType1.getId())
                                .content(objectMapper.writeValueAsString(validDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        result.andExpect(jsonPath("$.documentDate").value("2025-01-01"))
                .andExpect(jsonPath("$.additionalInformation.key").value("value"));
    }
    
    @Test
    public void testFailedDocumentUpdate() throws Exception {
        DocumentsTypeUpdateDto invalidDto = new DocumentsTypeUpdateDto();
        invalidDto.setDocumentDate(null);
        invalidDto.setDocumentScan(null);
        invalidDto.setAdditionalInformation(null);
        
        ResultActions result = mockMvc.perform(
                        patch("/api/v1/customers/" + customer.getId() + "/documents-types/" + documentType1.getId())
                                .content(objectMapper.writeValueAsString(invalidDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        
        result.andExpect(jsonPath("$.error", Matchers.is("Validation Errors")))
                .andExpect(jsonPath("$.message", Matchers.containsString("Дата документа не должна быть пустой")))
                .andExpect(jsonPath("$.message", Matchers.containsString("Копия документа не должна быть пустой")));
    }
}
