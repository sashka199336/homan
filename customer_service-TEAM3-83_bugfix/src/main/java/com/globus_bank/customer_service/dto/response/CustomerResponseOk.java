package com.globus_bank.customer_service.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class CustomerResponseOk {

    private UUID customerId;

    private String inn;

    private String customerType;

    private String status;

    private String createdAt;

    private String updatedAt;
}
