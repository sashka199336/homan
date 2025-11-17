package com.globus.financesystem.service;

import com.globus.financesystem.kafka.dto.OpenAccountDto;

public interface AccountService {
    void createAccount(OpenAccountDto dto);
}