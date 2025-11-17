package com.globus_bank.customer_service.customer_service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globus_bank.customer_service.dto.common.ContactsDto;
import com.globus_bank.customer_service.dto.update.ContactsUpdateDto;
import com.globus_bank.customer_service.entity.ContactsEntity;
import com.globus_bank.customer_service.entity.CustomerEntity;
import com.globus_bank.customer_service.entity.enums.Channel;
import com.globus_bank.customer_service.repository.ContactRepository;
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
public class ContactControllerIntegrationTest extends IntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private TestDataUtils testDataUtils;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ContactRepository contactRepository;

    private CustomerEntity customer;
    
    private ContactsEntity contact1;
    
    private ContactsEntity contact2;
    
    @BeforeEach
    void setupData() {
        customer = customerRepository.save(TestDataUtils.createCustomer());

        contact1 = TestDataUtils.createContact();
        contact1.setCustomer(customer);
        contact1 = contactRepository.save(contact1);

        contact2 = TestDataUtils.createContact();
        contact2.setCustomer(customer);
        contact2 = contactRepository.save(contact2);
    }
    
    @Test
    void whenGetContactsByCustomerId_thenReturnContacts() throws Exception {
        mockMvc.perform(get("/api/v1/customers/{customerId}/contacts", customer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    List<ContactsDto> contacts = objectMapper.readValue(json, new TypeReference<>() {
                    });
                    assertThat(contacts).hasSize(2);
                    assertThat(contacts).extracting("channel")
                            .containsExactlyInAnyOrder(contact1.getChannel(), contact2.getChannel());
                    assertThat(contacts).extracting("phoneNumber")
                            .containsExactlyInAnyOrder(contact1.getPhoneNumber(), contact2.getPhoneNumber());
                    assertThat(contacts).extracting("email")
                            .containsExactlyInAnyOrder(contact1.getEmail(), contact2.getEmail());
                });
    }
    
    @Test
    void whenGetContactsByNonExistingCustomer_thenReturnNotFound() throws Exception {
        UUID nonExistingCustomerId = UUID.randomUUID();
        
        mockMvc.perform(get("/api/v1/customers/{customerId}/contacts", nonExistingCustomerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void testSuccessfulContactUpdate() throws Exception {
        ContactsUpdateDto validDto = new ContactsUpdateDto();
        validDto.setPhoneNumber("+79123456789");
        validDto.setEmail("example@example.com");
        validDto.setChannel(Channel.EMAIL);
        
        ResultActions result = mockMvc.perform(
                        patch("/api/v1/customers/" + customer.getId() + "/contacts/" + contact1.getId())
                                .content(objectMapper.writeValueAsString(validDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        result.andExpect(jsonPath("$.phoneNumber").value("+79123456789"))
                .andExpect(jsonPath("$.email").value("example@example.com"))
                .andExpect(jsonPath("$.channel").value("EMAIL"));
    }
    
    @Test
    public void testFailedContactUpdate() throws Exception {
        ContactsUpdateDto invalidDto = new ContactsUpdateDto();
        invalidDto.setPhoneNumber("");
        invalidDto.setEmail("bad_email");
        invalidDto.setChannel(null);
        
        ResultActions result = mockMvc.perform(
                        patch("/api/v1/customers/" + customer.getId() + "/contacts/" + contact1.getId())
                                .content(objectMapper.writeValueAsString(invalidDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        
        result.andExpect(jsonPath("$.error", Matchers.is("Validation Errors")))
                .andExpect(jsonPath("$.message", Matchers.containsString("Номер телефона не должен быть пустым")))
                .andExpect(jsonPath("$.message", Matchers.containsString("Электронная почта должна быть валидной")))
                .andExpect(jsonPath("$.message", Matchers.containsString("Канал должен быть указан")));
    }
}
