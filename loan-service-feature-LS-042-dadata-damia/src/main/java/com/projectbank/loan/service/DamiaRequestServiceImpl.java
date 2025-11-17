package com.projectbank.loan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectbank.loan.dto.DamiaRequestDto;
import com.projectbank.loan.entity.LoanApplication;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DamiaRequestServiceImpl implements DamiaRequestService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public void sendDamiaRequest(LoanApplication entity) {
        DamiaRequestDto damiaRequestDto = new DamiaRequestDto(
                entity.getId(),
                List.of(entity.getPassportNumber()),
                entity.getInnOrOgrn()
        );

        String content = objectMapper.writeValueAsString(damiaRequestDto);
        kafkaTemplate.send("damia-request-topic", content);
    }
}
