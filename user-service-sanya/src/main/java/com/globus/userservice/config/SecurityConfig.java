package com.globus.userservice.config;

import com.globus.userservice.service.impl.UserServiceImpl;
import com.globus.userservice.util.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@PropertySource("secrets.properties")
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasRole("admin")
//                        .requestMatchers(HttpMethod.POST, "/api/v1/users/register").hasRole("admin")
//                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/**/role/add").hasRole("admin")
//                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/**/role/remove").hasRole("admin")
//                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/**/privilege/add").hasRole("admin")
//                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/**/privilege/remove").hasRole("admin")
//                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/**").hasRole("admin")
//                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/password/change").hasRole("admin")
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/api/v1/auth/login",
                                "/auth/validate",
                                "/swagger-resources/**",
                                "/swagger-ui/index.html",
                                "/v3/api-docs.yaml",
                                "/users/register",
                                "/error"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserServiceImpl userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authenticationProvider);
    }
}