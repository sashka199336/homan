package com.globus_bank.customer_service.customer_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globus_bank.customer_service.dto.common.*;
import com.globus_bank.customer_service.dto.kafka.NotificationRuleDto;
import com.globus_bank.customer_service.dto.response.CustomerCreatedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.concurrent.TimeUnit;

@AutoConfigureMockMvc
public class CustomerControllerSendKafkaTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestKafkaListener testKafkaListener;

    private FullCustomerDto fullCustomerDto;

    @BeforeEach
    void setupData() {
        fullCustomerDto = TestDataUtils.createFullCustomerDto();
    }

    @Test
    public void whenCreateCustomer_thenKafkaSend() throws Exception {
        Thread.sleep(5000);
        LegalDto legalDto = TestDataUtils.createLegalDto();
        fullCustomerDto.setLegalEntity(legalDto);

        String responseString = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fullCustomerDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CustomerCreatedResponse response = objectMapper.readValue(responseString, CustomerCreatedResponse.class);
        UUID customerId = response.customerId();

        NotificationRuleDto received = testKafkaListener.getRecords().poll(60, TimeUnit.SECONDS);

        assertThat(received).isNotNull();
        assertThat(received.getClientId()).isEqualTo(customerId.toString());
        assertThat(received.getEmail()).isEqualTo(fullCustomerDto.getContacts().getFirst().getEmail());
        assertThat(received.getPhone()).isEqualTo(fullCustomerDto.getContacts().getFirst().getPhoneNumber());
        assertThat(received.getPreferNotificationChannels().contains(fullCustomerDto.getContacts().getFirst().getChannel()));

        received = testKafkaListener.getRecords().poll(60, TimeUnit.SECONDS);

        assertThat(received).isNotNull();
        assertThat(received.getClientId()).isEqualTo(customerId.toString());
        assertThat(received.getEmail()).isEqualTo(fullCustomerDto.getContacts().getLast().getEmail());
        assertThat(received.getPhone()).isEqualTo(fullCustomerDto.getContacts().getLast().getPhoneNumber());
        assertThat(received.getPreferNotificationChannels().contains(fullCustomerDto.getContacts().getLast().getChannel()));
    }
}
