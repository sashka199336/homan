package com.globus_bank.customer_service.customer_service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globus_bank.customer_service.dto.response.AddressResponseOk;
import com.globus_bank.customer_service.dto.update.AddressUpdateDto;
import com.globus_bank.customer_service.entity.AddressEntity;
import com.globus_bank.customer_service.entity.CustomerEntity;
import com.globus_bank.customer_service.entity.enums.AddressType;
import com.globus_bank.customer_service.repository.AddressRepository;
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
public class AddressControllerIntegrationTest extends IntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    private CustomerEntity customer;
    
    private AddressEntity address1;
    
    private AddressEntity address2;
    
    @BeforeEach
    void setupData() {
        customer = customerRepository.save(TestDataUtils.createCustomer());

        address1 = TestDataUtils.createAddress();
        address1.setCustomer(customer);
        address1 = addressRepository.save(address1);

        address2 = TestDataUtils.createAddress();
        address2.setCustomer(customer);
        address2 = addressRepository.save(address2);
    }
    
    @Test
    void whenGetAddressesByCustomerId_thenReturnAddresses() throws Exception {
        mockMvc.perform(get("/api/v1/customers/{customerId}/addresses", customer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    List<AddressResponseOk> addresses = objectMapper.readValue(json, new TypeReference<>() {
                    });
                    assertThat(addresses).hasSize(2);
                    assertThat(addresses).extracting("street").containsExactlyInAnyOrder(address1.getStreet(),
                            address2.getStreet());
                    assertThat(addresses).extracting("city").containsExactlyInAnyOrder(address1.getCity(),
                            address2.getCity());
                    assertThat(addresses).extracting("postalCode").containsExactlyInAnyOrder(address1.getPostalCode(),
                            address2.getPostalCode());
                    assertThat(addresses).extracting("apartmentNumber").containsExactlyInAnyOrder(address1.getApartmentNumber(),
                            address2.getApartmentNumber());
                    assertThat(addresses).extracting("houseNumber").containsExactlyInAnyOrder(address1.getHouseNumber(),
                            address2.getHouseNumber());
                    assertThat(addresses).extracting("country").containsExactlyInAnyOrder(address1.getCountry(),
                            address2.getCountry());
                    assertThat(addresses).extracting("addressType").containsExactlyInAnyOrder(address1.getAddressType().toString(),
                            address2.getAddressType().toString());

                });
    }
    
    @Test
    void whenGetAddressesByNonExistingCustomer_thenReturnNotFound() throws Exception {
        UUID nonExistingCustomerId = UUID.randomUUID();
        
        mockMvc.perform(get("/api/v1/customers/{customerId}/addresses", nonExistingCustomerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void testSuccessfulAddressUpdate() throws Exception {
        AddressUpdateDto updatedDto = new AddressUpdateDto();
        updatedDto.setCountry("new country");
        updatedDto.setCity("new city");
        updatedDto.setStreet("new street");
        updatedDto.setHouseNumber("new houseNumber");
        updatedDto.setPostalCode("12345");
        updatedDto.setAddressType(AddressType.ACTUAL);
        
        ResultActions result = mockMvc.perform(
                        patch("/api/v1/customers/" + customer.getId() + "/addresses/" + address1.getId())
                                .content(objectMapper.writeValueAsString(updatedDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        result.andExpect(jsonPath("$.country").value("new country"))
                .andExpect(jsonPath("$.city").value("new city"))
                .andExpect(jsonPath("$.street").value("new street"))
                .andExpect(jsonPath("$.houseNumber").value("new houseNumber"))
                .andExpect(jsonPath("$.postalCode").value("12345"));
    }
    
    @Test
    public void testFailedAddressUpdate() throws Exception {
        AddressUpdateDto invalidDto = new AddressUpdateDto();
        invalidDto.setCountry("new country");
        invalidDto.setCity("new city");
        invalidDto.setStreet("new street");
        invalidDto.setHouseNumber("");
        invalidDto.setPostalCode("abc");
        invalidDto.setAddressType(AddressType.ACTUAL);
        
        ResultActions result = mockMvc.perform(patch("/api/v1/customers/" + customer.getId() + "/addresses/" + address1.getId())
                        .content(objectMapper.writeValueAsString(invalidDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        
        result.andExpect(jsonPath("$.error", Matchers.is("Validation Errors")))
                .andExpect(jsonPath("$.message", Matchers.containsString("Номер дома обязателен")))
                .andExpect(jsonPath("$.message", Matchers.containsString("Почтовый код содержит 5 или 6 цифр")));
    }
}