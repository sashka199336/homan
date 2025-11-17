package com.globus.claim_service.feign;

import com.globus.claim_service.dto.customer.CustomerProfileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@FeignClient(name = "customer-service", url = "${app.feignClient.url}")
public interface CustomerFeignClient {

    @GetMapping("/api/v1/customers/{customerId}")
    CustomerProfileDto getCustomerById(@PathVariable UUID customerId);
}
