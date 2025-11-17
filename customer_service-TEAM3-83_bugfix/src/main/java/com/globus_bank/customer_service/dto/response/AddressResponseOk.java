package com.globus_bank.customer_service.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class AddressResponseOk {

    private UUID id;

    private String country;

    private String city;

    private String street;

    private String houseNumber;

    private String apartmentNumber;

    private String postalCode;

    private String addressType;
}
