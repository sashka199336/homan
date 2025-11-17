package com.api.gateway.filter;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

class JwtRoleFilterTest {

    private final JwtRoleFilter filter = new JwtRoleFilter();

    private Jwt buildJwt(String sub, List<String> roles) {
        return new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"),
                Map.of("sub", sub, "roles", roles)
        );
    }

    private JwtAuthenticationToken auth(String sub, List<String> roles) {
        return new JwtAuthenticationToken(
                buildJwt(sub, roles),
                roles.stream()
                        .map(r -> "ROLE_" + r.toUpperCase())
                        .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                        .toList()
        );
    }

    @Test
    void apply_whenPrincipalIsNotJwt_shouldReturnUnauthorized() {
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/test").build()
        );

        exchange = exchange.mutate()
                .principal(Mono.just(() -> "not-a-jwt"))
                .build();

        GatewayFilterChain chain = Mockito.mock(GatewayFilterChain.class);

        filter.apply(new Object()).filter(exchange, chain).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        Mockito.verify(chain, Mockito.never()).filter(any(ServerWebExchange.class));
    }

    @Test
    void apply_withAdminRole_shouldAddHeaders() {
        JwtAuthenticationToken token = auth("admin1", List.of("ADMIN"));
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/admin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer 123")
                        .build()
        ).mutate().principal(Mono.just(token)).build();

        GatewayFilterChain chain = Mockito.mock(GatewayFilterChain.class);
        Mockito.when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        filter.apply(new Object()).filter(exchange, chain).block();

        Mockito.verify(chain).filter(Mockito.argThat(modified -> {
            HttpHeaders headers = modified.getRequest().getHeaders();
            assertThat(headers.getFirst("X-User-Id")).isEqualTo("admin1");
            assertThat(headers.getFirst("X-Admin-Id")).isEqualTo("admin1");
            assertThat(headers.getFirst("Auth")).isEqualTo("Bearer 123");
            assertThat(headers.getFirst(HttpHeaders.AUTHORIZATION)).isNull();
            return true;
        }));
    }

    @Test
    void apply_withUserRole_shouldAddOnlyUserHeader() {
        JwtAuthenticationToken token = auth("user1", List.of("USER"));
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/user").build()
        ).mutate().principal(Mono.just(token)).build();

        GatewayFilterChain chain = Mockito.mock(GatewayFilterChain.class);
        Mockito.when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        filter.apply(new Object()).filter(exchange, chain).block();

        Mockito.verify(chain).filter(Mockito.argThat(modified -> {
            assertThat(modified.getRequest().getHeaders().getFirst("X-User-Id")).isEqualTo("user1");
            assertThat(modified.getRequest().getHeaders().getFirst("X-Admin-Id")).isNull();
            return true;
        }));
    }

    @Test
    void apply_withNoRoles_shouldNotAddHeaders() {
        JwtAuthenticationToken token = auth("guest", List.of());
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/guest").build()
        ).mutate().principal(Mono.just(token)).build();

        GatewayFilterChain chain = Mockito.mock(GatewayFilterChain.class);
        Mockito.when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        filter.apply(new Object()).filter(exchange, chain).block();

        Mockito.verify(chain).filter(Mockito.argThat(modified -> {
            HttpHeaders headers = modified.getRequest().getHeaders();
            assertThat(headers.getFirst("X-User-Id")).isNull();
            assertThat(headers.getFirst("X-Admin-Id")).isNull();
            return true;
        }));
    }
}