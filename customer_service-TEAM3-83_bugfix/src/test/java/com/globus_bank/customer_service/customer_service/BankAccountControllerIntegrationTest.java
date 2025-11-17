package com.globus_bank.customer_service.customer_service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globus_bank.customer_service.dto.response.BankAccountResponseOk;
import com.globus_bank.customer_service.dto.update.BankAccountUpdateDto;
import com.globus_bank.customer_service.entity.BankAccountEntity;
import com.globus_bank.customer_service.entity.CustomerEntity;
import com.globus_bank.customer_service.repository.BankAccountRepository;
import com.globus_bank.customer_service.repository.CustomerRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class BankAccountControllerIntegrationTest extends IntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    private CustomerEntity customer;
    
    private BankAccountEntity bankAccount1;
    
    private BankAccountEntity bankAccount2;
    
    @BeforeEach
    void setupData() {
        customer = customerRepository.save(TestDataUtils.createCustomer());

        bankAccount1 = TestDataUtils.createBankAccount();
        bankAccount1.setCustomer(customer);
        bankAccount1 = bankAccountRepository.save(bankAccount1);

        bankAccount2 = TestDataUtils.createBankAccount();
        bankAccount2.setCustomer(customer);
        bankAccount2 = bankAccountRepository.save(bankAccount2);
    }
    
    @Test
    void whenGetBankAccountsByCustomerId_thenReturnBankAccounts() throws Exception {
        mockMvc.perform(get("/api/v1/customers/{customerId}/bank-accounts", customer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    List<BankAccountResponseOk> bankAccounts = objectMapper.readValue(json, new TypeReference<>() {
                    });
                    assertThat(bankAccounts).hasSize(2);
                    assertThat(bankAccounts).extracting("settlementAccount")
                            .containsExactlyInAnyOrder(bankAccount1.getSettlementAccount(), bankAccount2.getSettlementAccount());
                    assertThat(bankAccounts).extracting("bic")
                            .containsExactlyInAnyOrder(bankAccount1.getBic(), bankAccount2.getBic());
                    assertThat(bankAccounts).extracting("correspondentAccount")
                            .containsExactlyInAnyOrder(bankAccount1.getCorrespondentAccount(), bankAccount2.getCorrespondentAccount());
                });
    }
    
    @Test
    void whenGetBankAccountsByNonExistingCustomer_thenReturnNotFound() throws Exception {
        UUID nonExistingCustomerId = UUID.randomUUID();
        
        mockMvc.perform(get("/api/v1/customers/{customerId}/bank-accounts", nonExistingCustomerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void testSuccessfulBankAccountUpdate() throws Exception {
        BankAccountUpdateDto validDto = new BankAccountUpdateDto();
        validDto.setSettlementAccount("12345678901234567890");
        validDto.setCorrespondentAccount("98765432109876543210");
        validDto.setBic("044525225");
        
        ResultActions result = mockMvc.perform(
                        patch("/api/v1/customers/" + customer.getId() + "/bank-accounts/" + bankAccount1.getId())
                                .content(objectMapper.writeValueAsString(validDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        result.andExpect(jsonPath("$.settlementAccount").value("12345678901234567890"))
                .andExpect(jsonPath("$.correspondentAccount").value("98765432109876543210"))
                .andExpect(jsonPath("$.bic").value("044525225"));
    }
    
    @Test
    public void testFailedBankAccountUpdate() throws Exception {
        BankAccountUpdateDto invalidDto = new BankAccountUpdateDto();
        invalidDto.setSettlementAccount("");
        invalidDto.setCorrespondentAccount("abc");
        invalidDto.setBic("123");
        
        ResultActions result = mockMvc.perform(
                        patch("/api/v1/customers/" + customer.getId() + "/bank-accounts/" + bankAccount1.getId())
                                .content(objectMapper.writeValueAsString(invalidDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        
        result.andExpect(jsonPath("$.error", Matchers.is("Validation Errors")))
                .andExpect(jsonPath("$.message", Matchers.containsString("Расчетный счет не должен быть пустым")))
                .andExpect(jsonPath("$.message", Matchers.containsString("Корреспондентский счет должен содержать ровно 20 цифр")))
                .andExpect(jsonPath("$.message", Matchers.containsString("БИК должен содержать от 9 до 11 буквенно-цифровых символов")));
    }
}
