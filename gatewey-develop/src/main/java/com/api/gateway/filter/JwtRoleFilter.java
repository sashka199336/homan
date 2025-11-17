package com.api.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtRoleFilter extends AbstractGatewayFilterFactory<Object> {

    public JwtRoleFilter() {
        super(Object.class);
    }

    public GatewayFilter apply(Object config) {

        return (exchange, chain) -> exchange.getPrincipal()
                .flatMap(principal -> {
                    if (!(principal instanceof JwtAuthenticationToken)) {
                        exchange.getResponse()
                                .setStatusCode(HttpStatus.UNAUTHORIZED);
                        log.warn(
                                "Unauthorized: Principal is not JWT token for path: {}",
                                exchange.getRequest().getPath());
                        return exchange.getResponse().setComplete();
                    }
                    JwtAuthenticationToken auth =
                            (JwtAuthenticationToken) principal;
                    String userId = auth.getToken().getSubject();
                    List<String> authorities = auth.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList());
                    log.info("User {} with roles {} accessing {}", userId,
                            authorities, exchange.getRequest().getPath());

                    boolean isAdmin = authorities.contains("ROLE_ADMIN");
                    boolean isUser = authorities.contains("ROLE_USER");
                    if (isAdmin || isUser) {
                        log.debug(
                                "Access granted for user {} to {} (admin: {}, user: {})",
                                userId, exchange.getRequest().getPath(),
                                isAdmin, isUser);
                        ServerWebExchange modifiedExchange = exchange.mutate()
                                .request(builder -> builder.headers(headers -> {
                                    headers.set("X-User-Id", userId);
                                    if (isAdmin) {
                                        headers.set("X-Admin-Id", userId);
                                        String authHeader =
                                                exchange.getRequest()
                                                        .getHeaders().getFirst(
                                                                HttpHeaders.AUTHORIZATION);
                                        if (authHeader != null) {
                                            headers.set("Auth", authHeader);
                                        }
                                    }
                                    headers.remove(HttpHeaders.AUTHORIZATION);
                                })).build();
                        log.debug("Added headers: X-User-Id={}, isAdmin={}",
                                userId, isAdmin);
                        return chain.filter(modifiedExchange);
                    }
                    log.debug("Standard processing for user {} to {}", userId,
                            exchange.getRequest().getPath());

                    log.debug(
                            "Access granted for user {} to {} (admin: {}, user: {}, target: {})",
                            userId, exchange.getRequest().getPath(), isAdmin,
                            isUser);
                    log.debug(
                            "Added headers for user: X-User-Id={}, isAdmin={}",
                            userId, isAdmin);

                    return chain.filter(exchange);
                });
    }
}
