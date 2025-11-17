package com.globus.financesystem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Slf4j
@EnableScheduling
public class FinanceSystemApplication {
    public static void main(String[] args) {
        log.info("Запуск приложения FinanceSystem...");
        SpringApplication.run(FinanceSystemApplication.class, args);
        log.info("Приложение FinanceSystem успешно запущено.");
    }
}