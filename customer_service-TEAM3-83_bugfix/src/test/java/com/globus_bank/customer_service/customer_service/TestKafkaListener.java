package com.globus_bank.customer_service.customer_service;

import com.globus_bank.customer_service.dto.kafka.NotificationRuleDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class TestKafkaListener{

    private BlockingQueue<NotificationRuleDto> records = new LinkedBlockingQueue<>();

    @KafkaListener(id = "testKafkaListener", topics = "notifications-rule-topic", groupId = "test-group", autoStartup = "true")
    public void listen(NotificationRuleDto dto) {
        records.add(dto);
    }

    public BlockingQueue<NotificationRuleDto> getRecords() {
        return records;
    }
}
