package com.globus.userservice.util;

import com.globus.userservice.entity.Privilege;
import com.globus.userservice.entity.Role;
import com.globus.userservice.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

@Component
public class JwtTokenUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Integer lifetime;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        List<String> rolesList = user.getRoles()
                .stream().map(Role::getRoleName).toList();
        List<String> privilegesList = user.getPrivileges()
                .stream().map(Privilege::getName).toList();
        claims.put("userId", user.getId());
        claims.put("roles", rolesList);
        claims.put("privileges", privilegesList);

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + lifetime);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public List<String> getRoles(String token) {
        return getClaimFromToken(token, (Function<Claims, List<String>>)
                claims -> claims.get("roles", List.class));
    }

    public List<String> getPrivileges(String token) {
        return getClaimFromToken(token, (Function<Claims, List<String>>)
                claims -> claims.get("privileges", List.class));
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Base64.getDecoder().decode(secret))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
