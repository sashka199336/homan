package com.globus_bank.customer_service.customer_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globus_bank.customer_service.dto.common.*;
import com.globus_bank.customer_service.dto.response.CustomerCreatedResponse;
import com.globus_bank.customer_service.dto.response.ErrorResponse;
import com.globus_bank.customer_service.entity.*;
import com.globus_bank.customer_service.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.*;

@AutoConfigureMockMvc
public class CustomerControllerCreateIntegrationTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private FullCustomerDto fullCustomerDto;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DocumentsTypesRepository documentsTypesRepository;

    @Autowired
    private IndividualEntrepreneurRepository individualEntrepreneurRepository;

    @Autowired
    private LegalRepository legalRepository;

    @Autowired
    private PassportRepository passportRepository;

    @AfterEach
    void cleanDatabase() {
        customerRepository.deleteAll();
    }

    @BeforeEach
    void setupData() {
        fullCustomerDto = TestDataUtils.createFullCustomerDto();
    }

    @Test
    void whenCreateCustomerLegal_thenReturnCreated() throws Exception {
        LegalDto legalDto = TestDataUtils.createLegalDto();
        fullCustomerDto.setLegalEntity(legalDto);

        String responseString = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fullCustomerDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        validateCustomerCreate(responseString);
    }

    @Test
    void whenCreateCustomerIndividual_thenReturnCreated() throws Exception {
        IndividualEntrepreneurDto individualEntrepreneurDto = TestDataUtils.createIndividualEntrepreneurDto();
        fullCustomerDto.setIndividualEntrepreneur(individualEntrepreneurDto);

        String responseString = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fullCustomerDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        validateCustomerCreate(responseString);
    }

    @Test
    void whenCreateCustomerWithInvalidData_thenReturnBadRequest() throws Exception {
        fullCustomerDto.getCustomer().setInn("1");

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fullCustomerDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreateCustomerWithExistingInn_thenReturnBadRequest() throws Exception {
        IndividualEntrepreneurDto individualEntrepreneurDto = TestDataUtils.createIndividualEntrepreneurDto();
        fullCustomerDto.setIndividualEntrepreneur(individualEntrepreneurDto);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fullCustomerDto)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fullCustomerDto)))
                .andExpect(status().isConflict())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    ErrorResponse errorResponse = objectMapper.readValue(json, new TypeReference<>() {});
                    assertEquals("Клиент с таким ИНН уже существует", errorResponse.getMessage());
                });
    }

    @Test
    void whenCreateCustomerIndividualLegal_thenReturnBadRequest() throws Exception {
        IndividualEntrepreneurDto individualEntrepreneurDto = TestDataUtils.createIndividualEntrepreneurDto();
        fullCustomerDto.setIndividualEntrepreneur(individualEntrepreneurDto);

        LegalDto legalDto = TestDataUtils.createLegalDto();
        fullCustomerDto.setLegalEntity(legalDto);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fullCustomerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    ErrorResponse errorResponse = objectMapper.readValue(json, new TypeReference<>() {});
                    assertEquals("Клиент должен быть либо ЮЛ, либо ИП", errorResponse.getMessage());
                });
    }

    @Test
    void whenCreateCustomerWithoutLegal_thenReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fullCustomerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    ErrorResponse errorResponse = objectMapper.readValue(json, new TypeReference<>() {});
                    assertEquals("Банк не обслуживает физических лиц", errorResponse.getMessage());
                });
    }

    private void validateCustomerCreate(String responseString) throws JsonProcessingException {
        CustomerCreatedResponse response = objectMapper.readValue(responseString, CustomerCreatedResponse.class);
        UUID customerId = response.customerId();

        List<AddressEntity> addressList = addressRepository.findAllByCustomerId(customerId);
        assertEquals(addressList.size(), fullCustomerDto.getAddresses().size());

        List<BankAccountEntity> bankAccountList = bankAccountRepository.findAllByCustomerId(customerId);
        assertEquals(bankAccountList.size(), fullCustomerDto.getBankAccounts().size());

        List<ContactsEntity> contactsList = contactRepository.findAllByCustomerId(customerId);
        assertEquals(contactsList.size(), fullCustomerDto.getContacts().size());

        List<DocumentsTypesEntity> documentsTypeList = documentsTypesRepository.findAllByCustomerId(customerId);
        assertEquals(documentsTypeList.size(), fullCustomerDto.getDocuments().size());

        Optional<CustomerEntity> customerEntity = customerRepository.findById(customerId);
        assertNotNull(customerEntity);
        assertEquals(customerEntity.get().getCustomerType(), fullCustomerDto.getCustomer().getCustomerType());
        assertEquals(customerEntity.get().getInn(), fullCustomerDto.getCustomer().getInn());
        assertEquals(customerEntity.get().getStatusType(), fullCustomerDto.getCustomer().getStatusType());

        if(fullCustomerDto.getLegalEntity() != null) {
            Optional<LegalEntity> legalEntity = legalRepository.findByCustomerId(customerId);
            assertNotNull(legalEntity);
            assertEquals(legalEntity.get().getCompanyName(), fullCustomerDto.getLegalEntity().getCompanyName());
            assertEquals(legalEntity.get().getOgrn(), fullCustomerDto.getLegalEntity().getOgrn());
            assertEquals(legalEntity.get().getKpp(), fullCustomerDto.getLegalEntity().getKpp());
            assertEquals(legalEntity.get().getPosition(), fullCustomerDto.getLegalEntity().getPosition());
        }

        if(fullCustomerDto.getIndividualEntrepreneur() != null){
            Optional<IndividualEntrepreneurEntity> individualEntrepreneur = individualEntrepreneurRepository.findByCustomerId(customerId);
            assertNotNull(individualEntrepreneur);
            assertEquals(individualEntrepreneur.get().getEntrepreneurName(), fullCustomerDto.getIndividualEntrepreneur().getEntrepreneurName());
            assertEquals(individualEntrepreneur.get().getOgrnip(), fullCustomerDto.getIndividualEntrepreneur().getOgrnip());
        }

        Optional<PassportEntity> passport = passportRepository.findByDocumentId(documentsTypeList.get(0)
                .getId());
        assertNotNull(passport);
        assertEquals(passport.get().getDateOfBirth(), fullCustomerDto.getPassport().getDateOfBirth());
        assertEquals(passport.get().getIssueCode(), fullCustomerDto.getPassport().getIssueCode());
        assertEquals(passport.get().getIssueDate(), fullCustomerDto.getPassport().getIssueDate());
        assertEquals(passport.get().getNumber(), fullCustomerDto.getPassport().getNumber());
        assertEquals(passport.get().getSeries(), fullCustomerDto.getPassport().getSeries());
        assertEquals(passport.get().getFirstName(), fullCustomerDto.getPassport().getFirstName());
        assertEquals(passport.get().getLastName(), fullCustomerDto.getPassport().getLastName());
        assertEquals(passport.get().getPatronymic(), fullCustomerDto.getPassport().getPatronymic());
    }
}
