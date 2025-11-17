package com.globus.session_tracing.services;

import com.globus.session_tracing.entities.Session;
import com.globus.session_tracing.exceptions.SessionNotFoundException;
import com.globus.session_tracing.exceptions.TooManySessionsException;
import com.globus.session_tracing.repositiries.RedisRepository;
import com.globus.session_tracing.repositiries.SessionRepository;
import com.globus.session_tracing.repositiries.specifications.SessionSpecification;
import com.globus.session_tracing.utils.Base64Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SessionTracingService {
    private final SessionRepository sessionRepository;
    private final RedisRepository redisRepository;

    @Value("${sessions.page.quantity}")
    private int pageSize;
    @Value("${sessions.life.days}")
    private int sessionLifeDays;

    public Page<Session> findAll(Integer userId, LocalDateTime minLoginTime, String method,
                                 Boolean isActive, Integer page, String sort) {
        Specification<Session> specification = (root, query, builder) -> null;
        if (userId != null) {
            specification = specification.and(SessionSpecification.userIdEqualTo(userId));
        }
        if (minLoginTime != null) {
            specification = specification.and(SessionSpecification.loginTimeGreaterOrEqualThan(minLoginTime));
        }
        if (method != null && !method.isBlank()) {
            specification = specification.and(SessionSpecification.methodEqualTo(method));
        }
        if (isActive != null) {
            specification = specification.and(SessionSpecification.activityEqualTo(isActive));
        }
        if (page < 1) page = 1;
        return sessionRepository.findAll(
                specification,
                PageRequest.of(page - 1, pageSize, Sort.by(sort))
        ).map(this::decode);
    }

    public Session findBySessionId(long id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new SessionNotFoundException(
                        String.format("Сессия с id: %d не найдена.", id)));
        return decode(session);
    }

    @Transactional
    public Session save(Session session) {
        try {
            logout(session.getUserId(), session.getDeviceInfo());
        } catch (SessionNotFoundException e) {
            log.info(String.format("Активных сессий с userID: %d и deviceInfo: %s не найдено",
                    session.getUserId(), session.getDeviceInfo()));
        }
        List<Session> sessions = redisRepository.findAllByUserId(session.getUserId());
        if (sessions.size() >= 3) {
            throw new TooManySessionsException("Открыто слишком много сессий.");
        }
        session.setId(null);
        session.setIsActive(true);
        session.setDeviceInfo(Base64Service.encode(session.getDeviceInfo()));
        session.setIpAddress(Base64Service.encode(maskIp(session.getIpAddress())));
        session = sessionRepository.save(session);
        if (session.getId() != null) {
            redisRepository.add(session);
        }
        return session;
    }

    @Transactional
    public void logout(Integer userId, String deviceInfo) throws SessionNotFoundException {
        List<Session> sessions = findAllSessionsByUserIdAndDeviceInfo(userId, deviceInfo);
        if (sessions.isEmpty()) {
            throw new SessionNotFoundException(
                    String.format("Активных сессий с userID: %d и deviceInfo: %s не найдено", userId, deviceInfo));
        }
        for (Session session : sessions) {
            sessionRepository.logout(session.getId());
            redisRepository.delete(session.getId());
        }
    }

    private List<Session> findAllSessionsByUserIdAndDeviceInfo(Integer userId, String deviceInfo) {
        String codeDeviceInfo = Base64Service.encode(deviceInfo);
        return redisRepository.findAllByUserId(userId)
                .stream()
                .filter(s -> codeDeviceInfo.equals(s.getDeviceInfo()))
                .toList();
    }

    public List<Session> findAllFromRedis() {
        return redisRepository.findAll().stream().map(this::decode).toList();
    }

    public Session findFromRedisById(long id) {
        Session session = redisRepository.findBySessionId(id)
                .orElseThrow(() -> new SessionNotFoundException(
                        String.format("Сессия с id: %d не найдена.", id)));
        return decode(session);
    }

    public void prolongSession(Integer userId, String deviceInfo) {
        List<Session> sessions = findAllSessionsByUserIdAndDeviceInfo(userId, deviceInfo);
        if (sessions.isEmpty()) {
            throw new SessionNotFoundException(
                    String.format("Активных сессий с userID: %d и deviceInfo: %s не найдено", userId, deviceInfo));
        }
        for (Session session : sessions) {
            redisRepository.prolongSession(session.getId());
        }
    }

    @Scheduled(cron = "${sessions.life.task.cron.delete-old}")
    public void deleteOldSessions() {
        LocalDateTime date = LocalDateTime.now().minusDays(sessionLifeDays);
        sessionRepository.deleteOldSessions(date);
        log.info(String.format("Удаление сессий, созданных больше %d дней назад.", sessionLifeDays));
    }

    @Scheduled(cron = "${sessions.life.task.cron.delete-not-active}")
    public void deleteNotActiveSessions() {
        List<Long> keys = redisRepository.findAllKeys().stream()
                .filter(k -> k.matches("\\d+"))
                .map(Long::valueOf)
                .toList();
        sessionRepository.closeNotActiveSessions(keys);
        log.info("Закрытие сессий с истёкшим сроком ожидания.");
    }

    private String maskIp(String ip) {
        if (ip == null) return null;
        String[] parts = ip.split("\\.");
        if (parts.length != 4) return ip;
        String first = parts[0];
        String second = parts[1].isEmpty() ? "*" : parts[1].substring(0, 1);
        String fourth = parts[3];
        return String.format("%s.%s**.***.%s", first, second, fourth);
    }

    private Session decode(Session session) {
        if (session.getDeviceInfo() != null) {
            session.setDeviceInfo(Base64Service.decode(session.getDeviceInfo()));
        }
        if (session.getIpAddress() != null) {
            session.setIpAddress(Base64Service.decode(session.getIpAddress()));
        }
        return session;
    }
}