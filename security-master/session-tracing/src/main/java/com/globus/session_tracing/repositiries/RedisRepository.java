package com.globus.session_tracing.repositiries;

import com.globus.session_tracing.entities.Session;
import com.globus.session_tracing.exceptions.SessionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, Session> redisTemplate;

    @Value("${spring.data.redis.lifetime.minutes}")
    private int sessionLifeTimeMinutes;

    //  Метод для формирования ключа сессии
    private String sessionKey(Long sessionId) {
        return "session:" + sessionId;
    }

    // Добавить сессию к Redis-репозиторию
    public void add(Session session) {
        String key = sessionKey(session.getId());
        redisTemplate.opsForValue().set(key, session);
        redisTemplate.expire(key, sessionLifeTimeMinutes, TimeUnit.MINUTES);
    }

    // Продлить TTL сессии
    public void prolongSession(Long id) {
        String key = sessionKey(id);
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            throw new SessionNotFoundException("Сессия не найдена или уже завершена");
        }
        redisTemplate.expire(key, sessionLifeTimeMinutes, TimeUnit.MINUTES);
    }

    // Поиск сессии по идентификатору
    public Optional<Session> findBySessionId(long id) {
        Session session = redisTemplate.opsForValue().get(sessionKey(id));
        return Optional.ofNullable(session);
    }

    // Поиск всех сессий в Redis-репозитории
    public List<Session> findAll() {
        Set<String> redisKeys = redisTemplate.keys("session:*");
        List<Session> sessions = new ArrayList<>();
        if (redisKeys != null) {
            for (String redisKey : redisKeys) {
                Session session = redisTemplate.opsForValue().get(redisKey);
                if (session != null) {
                    sessions.add(session);
                }
            }
        }
        return sessions;
    }

    // Поиск всех идентификаторов сессий, находящихся в Redis-репозитории
    public Set<String> findAllKeys() {
        Set<String> keys = redisTemplate.keys("session:*");
        return keys != null ? keys : Collections.emptySet();
    }

    // Удаление сессии по идентификатору
    public boolean delete(long id) {
        Boolean res = redisTemplate.delete(sessionKey(id));
        return Boolean.TRUE.equals(res);
    }

    // Поиск всех сессий по идентификатору пользователя
    public List<Session> findAllByUserId(Integer userId) {
        Set<String> redisKeys = redisTemplate.keys("session:*");
        List<Session> sessions = new ArrayList<>();
        if (redisKeys != null) {
            for (String key : redisKeys) {
                Session session = redisTemplate.opsForValue().get(key);
                if (session != null && Objects.equals(session.getUserId(), userId)) {
                    sessions.add(session);
                }
            }
        }
        return sessions;
    }
}