package com.globus.payment_service.configuration.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

@Configuration
public class KafkaConfig {

    @Bean
    public JsonMessageConverter messageConverter(ObjectMapper mapper) {
        return new StringJsonMessageConverter(mapper);
    }
}
