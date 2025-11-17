package com.api.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
public class JwtAuthConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        log.debug("Converting JWT token for subject: {}", jwt.getSubject());
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        log.info("Successfully converted JWT for user {} with {} authorities",
                jwt.getSubject(), authorities.size());
        log.debug("User {} authorities: {}", jwt.getSubject(), authorities);
        return Mono.just(new JwtAuthenticationToken(jwt, authorities));
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        log.debug("Extracting authorities from JWT for subject: {}", jwt.getSubject());
        if (jwt.getClaim("roles") != null) {
            List<String> roles = jwt.getClaim("roles");
            return roles.stream()
                    .map(role -> {
                        String formattedRole = role.toUpperCase();
                        if (!formattedRole.startsWith("ROLE_")) {
                            formattedRole = "ROLE_" + formattedRole;
                        }
                        return formattedRole;
                    }).filter(role -> role.equals("ROLE_USER") || role.equals("ROLE_ADMIN"))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        log.warn("No authorities found in JWT for subject: {}", jwt.getSubject());
        return Collections.emptyList();
    }
}
