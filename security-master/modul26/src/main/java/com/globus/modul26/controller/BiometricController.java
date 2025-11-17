package com.globus.modul26.controller;

import com.globus.modul26.dto.SecurityLogDTO;
import com.globus.modul26.model.SecurityLog;
import com.globus.modul26.service.SecurityLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BiometricController {

    private final SecurityLogService logService;

    public BiometricController(SecurityLogService logService) {
        this.logService = logService;
    }

    @PostMapping("/biometric")
    public ResponseEntity<String> handleBiometric(@RequestBody SecurityLogDTO payload) {
        System.out.println("Получено событие biometric: " + payload);

        // Если это FAIL
        if ("LOGIN_BIOMETRIC_FAIL".equalsIgnoreCase(payload.getEventType())) {
            // 1. Логируем сам FAIL
            SecurityLog failLog = mapDtoToEntity(payload);
            failLog.setIsSuspicious(false);
            logService.saveLog(failLog);

            // Если фейлов подряд 3  логируование BLOCKED
            int failCount = logService.countRecentFails(payload.getUserId(), payload.getDeviceInfo());
            if (failCount >= 3) {
                SecurityLog blockedLog = mapDtoToEntity(payload);
                blockedLog.setEventType("LOGIN_BIOMETRIC_BLOCKED");
                blockedLog.setIsSuspicious(true);
                logService.saveLog(blockedLog);
            }
        }
        //  Если пришёл BLOCKED как отдельное событие
        else if ("LOGIN_BIOMETRIC_BLOCKED".equalsIgnoreCase(payload.getEventType())) {
            SecurityLog blockedLog = mapDtoToEntity(payload);
            blockedLog.setIsSuspicious(true);
            logService.saveLog(blockedLog);
        }
        //  Любые другие eventType
        else {
            SecurityLog log = mapDtoToEntity(payload);
            log.setIsSuspicious(false); // или своя логика
            logService.saveLog(log);
        }

        return ResponseEntity.ok("biometric event saved");
    }

    // Вспомогательный маппер DTO
    private SecurityLog mapDtoToEntity(SecurityLogDTO dto) {
        SecurityLog log = new SecurityLog();
        log.setUserId(dto.getUserId());
        log.setEventType(dto.getEventType());
        log.setIpAddress(dto.getIpAddress());
        log.setDeviceInfo(dto.getDeviceInfo());
        log.setBiometryUsed(dto.getBiometryUsed());
        log.setMetadata(dto.getMetadata());
        // Добавь что надо!
        return log;
    }
}