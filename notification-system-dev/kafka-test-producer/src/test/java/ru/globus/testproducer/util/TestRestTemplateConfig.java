package ru.globus.testproducer.util;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class TestRestTemplateConfig {

    @Bean
    public TestRestTemplate testRestTemplate() {
        TestRestTemplate restTemplate = new TestRestTemplate();
        restTemplate.getRestTemplate().getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate;
    }
}
