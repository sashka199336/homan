package com.globus.biometric_auth_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LoginManager {
    private final RedisTemplate<String, String> redisTemplate;
    private static final int MAX_ATTEMPTS = 3;
    private static final long BLOCK_DURATION_MINUTES = 15;

    // Префикс для всех ключей попыток логина
    private String loginAttemptsKey(Integer id) {
        return "login_attempts:" + id;
    }

    /**
     * Увеличивает количество попыток и возвращает их актуальное количество
     */
    public int incrementAttempts(Integer id) {
        String key = loginAttemptsKey(id);
        Long attempts = redisTemplate.opsForValue().increment(key, 1);

        // Обновить TTL только при первой попытке
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(key, BLOCK_DURATION_MINUTES, TimeUnit.MINUTES);
        }
        return attempts != null ? attempts.intValue() : 1; // возвращаем текущее количество попыток
    }

    /**
     * Проверкка  заблокирован ли пользователь по количеству попыток
     */
    public boolean isBlocked(Integer id) {
        String key = loginAttemptsKey(id);
        String attemptsStr = redisTemplate.opsForValue().get(key);
        int attempts = attemptsStr != null ? Integer.parseInt(attemptsStr) : 0;
        return attempts >= MAX_ATTEMPTS;
    }

    /**
     * Сброс попытки после успешного входа
     */
    public void resetAttempts(Integer id) {
        String key = loginAttemptsKey(id);
        redisTemplate.delete(key);
    }
}