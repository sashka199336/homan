package com.globus.damiaadapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DamiaAdapterMain {
    public static void main(String[] args) {
        SpringApplication.run(DamiaAdapterMain.class, args);
    }
}
