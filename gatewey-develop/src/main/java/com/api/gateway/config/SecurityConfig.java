package com.api.gateway.config;

import com.api.gateway.filter.JwtAuthConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig { @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            ReactiveClientRegistrationRepository clientRegistrationRepository) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers( "/actuator/**",
                                "/swagger", "/v3/api-docs","/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .pathMatchers(HttpMethod.GET, "/gateway/user-service/**").hasAnyRole("USER", "ADMIN")
                        .pathMatchers(HttpMethod.POST, "/gateway/user-service/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/gateway/user-service/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/gateway/user-service/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/gateway/claim-service/**").hasAnyRole("USER")
                        .pathMatchers(HttpMethod.POST, "/gateway/claim-service/**").hasAnyRole("USER")
                        .pathMatchers(HttpMethod.PUT, "/gateway/claim-service/**").hasAnyRole("USER")
                        .pathMatchers(HttpMethod.DELETE, "/gateway/claim-service/**").hasRole("USER")
                        .pathMatchers(HttpMethod.PATCH, "/gateway/claim-service/**").hasRole("USER")
                        .anyExchange().authenticated()
                ).oauth2Client(oauth2 -> oauth2
                        .clientRegistrationRepository(clientRegistrationRepository))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter())))
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }

    @Bean

    public JwtAuthConverter jwtAuthConverter() {
        return new JwtAuthConverter();
    }
}
