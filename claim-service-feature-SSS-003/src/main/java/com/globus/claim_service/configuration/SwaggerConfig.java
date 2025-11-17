package com.globus.claim_service.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(title = "Claim Service API", version = "1.0"),
        servers = @Server(url = "https://swagger-labs.globus-ltd.com/claim-service",
                description = "Production server")
)
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("open-api-group")
                .packagesToScan("com.globus.claim_service.controller")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        String serviceDescription = """
                Функции сервиса:
                  - Оформление заявок на кредит, открытие счета и переводов.
                  - Централизованное хранение заявок клиента и обновление статусов.
                  - Взаимодействие с кредитным, клиентским и сервисом счетов.
                  - Инициирование коммуникации с клиентами через систему уведомлений.
                """;

        String teamMembersDescription = """
                Проектная команда:
                  - Системные аналитики: Артём Дёмкин, Алексей Цигура-Косенко
                  - Java-разработчики: Доржо Цыренов
                """;

        return new OpenAPI()
                .info(new Info()
                        .title("Claim Service API")
                        .description(serviceDescription + "\n" + teamMembersDescription)
                        .version("1.0.0"));
    }

}
