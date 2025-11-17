package com.globus.modul26.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globus.modul26.dto.SecurityLogDTO;
import com.globus.modul26.dto.SuspicionDTO;
import com.globus.modul26.model.SecurityLog;
import com.globus.modul26.service.JwtBlacklistService;
import com.globus.modul26.service.JwtServiceImpl;
import com.globus.modul26.service.SecurityLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/logs")
public class SecurityLogController {

    private final SecurityLogService service;
    private final JwtBlacklistService jwtBlacklistService;
    private final JwtServiceImpl jwtService;

    private static final String INTERNAL_SECRET = "biometric-internal-secret-42";

    public SecurityLogController(SecurityLogService service,
                                 JwtBlacklistService jwtBlacklistService,
                                 JwtServiceImpl jwtService) {
        this.service = service;
        this.jwtBlacklistService = jwtBlacklistService;
        this.jwtService = jwtService;
    }

    //  ЭНДПОИНТ для обычных security event и BIOMETRIC event
    @Operation(
            summary = "Сохранить security event (универсальный)",
            description = "Логирование событий безопасности (LOGIN, LOGOUT, LOGIN_ATTEMPT, LOGIN_BIOMETRIC_FAIL, ...). Требуется JWT или секрет межсервисного доступа.",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "JWT токен пользователя в формате Bearer {token}",
                            required = false,
                            in = ParameterIn.HEADER,
                            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                    ),
                    @Parameter(
                            name = "User-Agent",
                            description = "Информация о клиентском устройстве или софте",
                            required = false,
                            in = ParameterIn.HEADER,
                            example = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
                    ),
                    @Parameter(
                            name = "X-Internal-Secret",
                            description = "Межсервисный секрет для внутренних вызовов (например, biometric-auth-service)",
                            required = false,
                            in = ParameterIn.HEADER,
                            example = "biometric-internal-secret-42"
                    )
            }
    )
    @PostMapping("/event")
    public ResponseEntity<?> logEvent(
            @RequestBody SecurityLog log,
            Authentication authentication,
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @RequestHeader(name = "X-Internal-Secret", required = false) String interSecret,
            @RequestHeader(value = "User-Agent", required = false) String userAgent
    ) {
        boolean isInternalCall = INTERNAL_SECRET.equals(interSecret);
        Long userId = null;

        if (!isInternalCall) {
            if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            userId = extractUserIdFromJwt((Jwt) authentication.getPrincipal());
            log.setUserId(userId);
        }

        if (log.getIpAddress() != null) {
            log.setIpAddress(maskIp(log.getIpAddress()));
        }
        log.setMetadata(normalizeAndMaskMetadataIp(log.getMetadata()));
        // LOGOUT
        if ("LOGOUT".equalsIgnoreCase(log.getEventType())) {
            tryBlacklistToken(authHeader);
        }

        // Расширенная логика для BIOMETRIC_FAIL/BLOCKED
        //
        if ("LOGIN_BIOMETRIC_FAIL".equalsIgnoreCase(log.getEventType())) {
            log.setIsSuspicious(false);
            SecurityLog savedFail = service.saveLog(cloneWithCurrentTime(log));
            // Проверяем кол-во подряд фейлов
            int failCount = service.countRecentFails(log.getUserId(), log.getDeviceInfo());
            if (failCount >= 3) {
                SecurityLog blockedLog = cloneWithCurrentTime(log);
                blockedLog.setEventType("LOGIN_BIOMETRIC_BLOCKED");
                blockedLog.setIsSuspicious(true);
                SecurityLog savedBlocked = service.saveLog(blockedLog);

                return ResponseEntity.status(HttpStatus.CREATED).body(Arrays.asList(savedFail, savedBlocked));
            } else {
                return ResponseEntity.status(HttpStatus.CREATED).body(savedFail);
            }
        } else if ("LOGIN_BIOMETRIC_BLOCKED".equalsIgnoreCase(log.getEventType())) {
            log.setIsSuspicious(true);
            SecurityLog saved = service.saveLog(cloneWithCurrentTime(log));
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } else {
            // Любой другой тип события
            log.setIsSuspicious(null); // если нужно — логика по конкретным типам
            SecurityLog saved = service.saveLog(cloneWithCurrentTime(log));
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        }
    }

    //  Функция-клонер
    private SecurityLog cloneWithCurrentTime(SecurityLog original) {
        SecurityLog log = new SecurityLog();
        log.setUserId(original.getUserId());
        log.setEventType(original.getEventType());
        log.setIpAddress(original.getIpAddress());
        log.setDeviceInfo(original.getDeviceInfo());
        log.setBiometryUsed(original.getBiometryUsed());
        log.setMetadata(original.getMetadata());
        log.setIsSuspicious(original.getIsSuspicious());
        log.setCreatedAt(LocalDateTime.now());
        log.setReason(original.getReason());
        return log;
    }



    @PostMapping("/suspect")
    public ResponseEntity<Void> markSuspicious(@RequestBody SuspicionDTO suspicion,
                                               @RequestHeader(name = "X-Internal-Secret", required = false) String interSecret) {
        if (!INTERNAL_SECRET.equals(interSecret)) {
            return ResponseEntity.status(403).build();
        }
        if (Boolean.TRUE.equals(suspicion.getSuspicion()) && suspicion.getUserId() != null) {
            SecurityLog log = new SecurityLog();
            log.setUserId(suspicion.getUserId());
            log.setEventType("LOGIN_BLOCKED");
            log.setIsSuspicious(true);
            log.setCreatedAt(LocalDateTime.now());
            log.setReason(suspicion.getReason());
            service.saveLog(log);

            System.out.printf("Поступила подозрительная активность (userId=%d)%n", suspicion.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Подозрительные события пользователя",
            description = "Получить список подозрительных (isSuspicious = true) событий по userId. Требуется JWT.",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "JWT токен пользователя (Bearer ...) для доступа",
                            required = true,
                            in = ParameterIn.HEADER,
                            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                    )
            }
    )
    @GetMapping("/suspicious/{userId}")
    public ResponseEntity<List<SecurityLog>> getSuspiciousByUser(
            @PathVariable Long userId,
            Authentication authentication,
            @RequestHeader(name = "Authorization", required = false) String authHeader
    ) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        List<String> rolesList = jwt.getClaimAsStringList("roles");
        boolean isAdmin = rolesList != null && rolesList.contains("ADMIN");
        boolean isUser = rolesList != null && rolesList.contains("USER");
        Long jwtUserId = extractUserIdFromJwt(jwt);

        if (isAdmin) {
            return ResponseEntity.ok(service.findSuspiciousLogsByUserId(userId));
        }
        if (isUser && Objects.equals(jwtUserId, userId)) {
            return ResponseEntity.ok(service.findSuspiciousLogsByUserId(userId));
        }
        throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
    }

    @Operation(
            summary = "Все события пользователя",
            description = "Получить все security-события по userId. Требуется JWT.",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "JWT токен пользователя (Bearer ...) для доступа",
                            required = true,
                            in = ParameterIn.HEADER,
                            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                    )
            }
    )
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<SecurityLog>> getByUser(
            @PathVariable Long userId,
            Authentication authentication,
            @RequestHeader(name = "Authorization", required = false) String authHeader
    ) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        List<String> rolesList = jwt.getClaimAsStringList("roles");
        boolean isAdmin = rolesList != null && rolesList.contains("ADMIN");
        boolean isUser = rolesList != null && rolesList.contains("USER");
        Long jwtUserId = extractUserIdFromJwt(jwt);

        if (isAdmin) {
            return ResponseEntity.ok(service.findByUserId(userId));
        }
        if (isUser && Objects.equals(jwtUserId, userId)) {
            return ResponseEntity.ok(service.findByUserId(userId));
        }
        throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
    }

    // Утилиты

    private static String maskIp(String ip) {
        if (ip == null) return null;
        String[] parts = ip.split("\\.");
        if (parts.length != 4) return ip;
        String first = parts[0];
        String second = parts[1].isEmpty() ? "*" : parts[1].substring(0, 1);
        String fourth = parts[3];
        return String.format("%s.%s**.***.%s", first, second, fourth);
    }

    private static Map<String, Object> normalizeAndMaskMetadataIp(Object metadataRaw) {
        Map<String, Object> metadataMap;
        if (metadataRaw == null) {
            metadataMap = new HashMap<>();
        } else if (metadataRaw instanceof Map) {
            metadataMap = new HashMap<>((Map<String, Object>) metadataRaw);
        } else {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                metadataMap = objectMapper.readValue(metadataRaw.toString(), Map.class);
            } catch (Exception e) {
                metadataMap = new HashMap<>();
            }
        }
        if (metadataMap.containsKey("ipAddress") && metadataMap.get("ipAddress") instanceof String) {
            metadataMap.put("ipAddress", maskIp((String) metadataMap.get("ipAddress")));
        }
        return metadataMap;
    }

    private static Long extractUserIdFromJwt(Jwt jwt) {
        try {
            return Long.parseLong(jwt.getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    private void tryBlacklistToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String jti = jwtService.extractJti(token);
            if (jti != null) {
                jwtBlacklistService.blacklist(jti);
                System.out.println("токен заблокирован по jti: " + jti);
            } else {
                System.out.println("LOGOUT: jti токена не найден, добавить в blacklist не удалось!");
            }
        } else {
            System.out.println("LOGOUT: Authorization header не найден или формат неверный.");
        }
    }
}