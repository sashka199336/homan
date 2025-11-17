package com.globus.financesystem.service;

import com.globus.financesystem.kafka.dto.TransferRequest;

public interface TransferService {
    void processTransferRequest(TransferRequest request);
}