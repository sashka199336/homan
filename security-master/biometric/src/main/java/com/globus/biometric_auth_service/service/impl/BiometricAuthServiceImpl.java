
package com.globus.biometric_auth_service.service.impl;

import com.globus.biometric_auth_service.dto.*;
import com.globus.biometric_auth_service.exception.IllegalAuthStateException;
import com.globus.biometric_auth_service.exception.UserNotFoundException;
import com.globus.biometric_auth_service.integration.*;
import com.globus.biometric_auth_service.mapper.BiometricSettingsMapper;
import com.globus.biometric_auth_service.model.*;
import com.globus.biometric_auth_service.repository.BiometricSettingsRepository;
import com.globus.biometric_auth_service.service.*;
import com.globus.biometric_auth_service.util.Base64Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.globus.biometric_auth_service.integration.SecurityLogClient;


import java.time.LocalDateTime;
import java.util.*;



@Service
@RequiredArgsConstructor
public class BiometricAuthServiceImpl implements BiometricAuthService {

    private final BiometricSettingsRepository settingsRepository;
    private final LoginManager loginManager;
    private final DeviceService deviceService;
    private final OtpService otpService;
    private final SmsService smsService;
    private final SessionTracingIntegrations sessionTracingIntegrations;
    private final Modul26Client modul26Client;
    private final SecurityLogClient securityLogClient;

    @Override
    public BiometricSettingsResponse enableBiometricAuth(BiometricRegisterRequest request) {
        if (!otpService.validateOtp(request.phoneNumber(), request.otp())) {
            throw new IllegalAuthStateException("Invalid OTP");
        }
        BiometricSettings settings = findByUserId(request.userId())
                .orElseGet(() -> BiometricSettings.builder()
                        .userId(request.userId())
                        .devices(new ArrayList<>())
                        .build());
        Device device = Device.builder()
                .account(settings)
                .deviceInfo(request.deviceInfo())
                .biometricEnabled(true)
                .biometryType(BiometryType.valueOf(request.biometricType()))
                .build();
        settings.getDevices().add(device);
        return BiometricSettingsMapper.getSettingsDto(settingsRepository.save(settings));
    }

    @Override
    public BiometricSettingsResponse getBiometricAuthStatus(Integer userId) {
        BiometricSettings settings = findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User with Id " + userId + " not found"));
        return BiometricSettingsMapper.getSettingsDto(settings);
    }

    @Override
    @Transactional
    public UserDetails biometricAuthLogin(BiometricAuthRequest request) {
        BiometricSettings settings = findByUserId(request.userId())
                .orElseThrow(() -> new UserNotFoundException("User with Id " + request.userId() + " not found"));
        checkDevice(settings, request.deviceInfo());
        checkFailedAttempts(request); // логирование фейлов и блокировки тут


        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                String.valueOf(request.userId()), // username
                "null",  // либо "", если password не нужен
                getAuthority(List.of("ROLE_USER"))
        );
        settings.setLastUsed(LocalDateTime.now());
        settingsRepository.save(settings);

        sessionTracingIntegrations.login(request.userId(), request.deviceInfo(), "biometric", "111.222.333.444");

        logSecurityEvent(request.userId(), request.deviceInfo(), "LOGIN_BIOMETRIC_SUCCESS", null, false);

        return userDetails;
    }

    private List<SimpleGrantedAuthority> getAuthority(List<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public Optional<BiometricSettings> findByUserId(Integer userId) {
        return settingsRepository.findByUserId(userId);
    }

    @Override
    public String requestBiometricAuth(BiometricRegisterRequest request) {
        String otp = otpService.generateOtp(request.phoneNumber());
        smsService.sendSms(request.phoneNumber(), "Your OTP is: " + Base64Service.decode(otp));
        return "OTP sent successfully: " + Base64Service.decode(otp);
    }

    @Override
    @Transactional
    public DeviceDto changeDeviceEnableStatus(DeviceStatusChangeRequest request) {
        Integer id = settingsRepository.findIdByUserId(request.userId())
                .orElseThrow(() -> new UserNotFoundException("User with Id " + request.userId() + " not found"));
        Device device = deviceService.changeDeviceEnableStatus(id, request.deviceInfo(), request.enabled());
        return BiometricSettingsMapper.getDeviceDto(device);
    }

    // Проверка включена ли биометрия
    private void checkDevice(BiometricSettings settings, String deviceInfo) {
        for (Device device : settings.getDevices()) {
            if (device.getDeviceInfo().equals(deviceInfo)) {
                if (!device.getBiometricEnabled()) {
                    throw new IllegalAuthStateException("У данного устройства выключена аутентификация с помощью биометрии.");
                }
                return;
            }
        }
        throw new IllegalAuthStateException("Данное устройство не зарегистрировано в системе аутентификации с помощью биометрии.");
    }

    //  лоигирование
    private void checkFailedAttempts(BiometricAuthRequest request) {
        Integer userIdInt = request.userId();
        Long userIdLong = (userIdInt != null) ? userIdInt.longValue() : null;

        // Проверка блокировки ДО попытки
        if (loginManager.isBlocked(userIdInt)) {
            logSecurityEvent(
                    userIdInt,
                    request.deviceInfo(),
                    "LOGIN_BIOMETRIC_BLOCKED",
                    "User blocked due to too many attempts",
                    true // suspicion
            );
            modul26Client.notifyBlockedUser(userIdLong, true);
            throw new IllegalAuthStateException("Too many invalid requests. Try again later.");
        }

        if (!request.authenticated()) {
            int attempts = loginManager.incrementAttempts(userIdInt);
            boolean nowBlocked = loginManager.isBlocked(userIdInt);

            if (nowBlocked) {
                logSecurityEvent(
                        userIdInt,
                        request.deviceInfo(),
                        "LOGIN_BIOMETRIC_BLOCKED",
                        "User blocked due to too many attempts",
                        true
                );
                modul26Client.notifyBlockedUser(userIdLong, true);
                throw new IllegalAuthStateException("Too many invalid requests. Try again later.");
            } else {
                logSecurityEvent(
                        userIdInt,
                        request.deviceInfo(),
                        "LOGIN_BIOMETRIC_FAIL",
                        "Failed login. Try again.",
                        false
                );
                throw new IllegalAuthStateException("Failed login. Try again.");
            }
        }
        loginManager.resetAttempts(userIdInt);
    }


    private void logSecurityEvent(Integer userId,
                                  String deviceInfo,
                                  String eventType,
                                  String description,
                                  boolean suspicious) {
        SecurityLog log = new SecurityLog();
        log.setUserId(userId != null ? userId.longValue() : null);
        log.setEventType(eventType);
        log.setIsSuspicious(suspicious);

        // 📝 Можно добавить info: device, description и т.д.
        log.setDeviceInfo(deviceInfo);
        log.setReason(description);

        try {
            securityLogClient.sendSecurityEvent(log);
        } catch (Exception ex) {
            System.err.println("Не удалось залогировать security event: " + ex.getMessage());
        }
    }
}