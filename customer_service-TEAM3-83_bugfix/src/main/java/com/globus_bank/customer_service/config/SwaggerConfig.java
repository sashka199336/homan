package com.globus_bank.customer_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(title = "Customer Service API", version = "1.0"),
        servers = @Server(url = "https://swagger-labs.globus-ltd.com/customer-service",
                description = "Production server")
)
public class SwaggerConfig {
    
    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("open-api-group")
                .packagesToScan("com.globus_bank.customer_service.controller")
                .build();
    }
    
    @Bean
    public OpenAPI customOpenAPI() {
        String serviceDescription = """
                Функции сервиса:
                  - Заведение нового клиента
                  - Предоставление клиентских данных
                  - Редактирование клиентских данных
                """;
        
        String teamMembersDescription = """
                Проектная команда:
                  - Системные аналитики: Георгий Ли, Вадим Ходос
                  - Java-разработчики: Елизавета Макарова, Олег Ковалев
                """;
        
        return new OpenAPI()
                .info(new Info()
                        .title("Customer Service API")
                        .description(serviceDescription + "\n" + teamMembersDescription)
                        .version("1.0.0"));
    }
}
