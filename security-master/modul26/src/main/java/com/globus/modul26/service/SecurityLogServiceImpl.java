package com.globus.modul26.service;

import com.globus.modul26.model.SecurityLog;
import com.globus.modul26.repository.SecurityLogRepository;
import com.globus.modul26.repository.BannedCountryRepository;
import com.globus.modul26.util.CefUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SecurityLogServiceImpl implements SecurityLogService {

    private final SecurityLogRepository repository;
    private final BannedCountryRepository bannedCountryRepository;
    private static final Logger cefLogger = LoggerFactory.getLogger("cefLogger");

    // Константы событий
    private static final String EVENT_LOGIN_ATTEMPT = "LOGIN_ATTEMPT";
    private static final String EVENT_LOGIN = "LOGIN";
    private static final String EVENT_PASSWORD_CHANGE = "PASSWORD_CHANGE";
    private static final int BLOCK_LIMIT = 3;

    public SecurityLogServiceImpl(SecurityLogRepository repository,
                                  BannedCountryRepository bannedCountryRepository) {
        this.repository = repository;
        this.bannedCountryRepository = bannedCountryRepository;
    }

    @Override
    @Transactional
    public void logEvent(Long userId, String eventType, String ipAddress, String deviceInfo, Boolean biometryUsed) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("country", "UNKNOWN");
        metadata.put("city", "UNKNOWN");

        SecurityLog log = SecurityLog.builder()
                .userId(userId)
                .eventType(eventType)
                .ipAddress(ipAddress)
                .deviceInfo(deviceInfo)
                .metadata(metadata)
                .biometryUsed(biometryUsed)
                .createdAt(LocalDateTime.now())
                .build();

        saveLog(log);
    }
    @Override
    public int countRecentFails(Long userId, String deviceInfo) {
        if (userId == null || deviceInfo == null) return 0;

        // загружаем последние событий
        List<SecurityLog> lastLogs = repository
                .findTop10ByUserIdAndDeviceInfoOrderByCreatedAtDesc(userId, deviceInfo);

        int count = 0;
        for (SecurityLog log : lastLogs) {
            String event = log.getEventType();
            if ("LOGIN_BIOMETRIC_FAIL".equals(event)) {
                count++; // увеличиваем, пока идут фейлы
            } else if ("LOGIN_BIOMETRIC_BLOCKED".equals(event)) {

                break;
            } else {

                break;
            }
        }
        return count;
    }

    @Override
    @Transactional
    public SecurityLog saveLog(SecurityLog log) {
        if (log.getCreatedAt() == null) {
            log.setCreatedAt(LocalDateTime.now());
        }
        // Метаданные по умолчанию
        if (log.getMetadata() == null) log.setMetadata(new HashMap<>());
        log.getMetadata().putIfAbsent("country", "UNKNOWN");
        log.getMetadata().putIfAbsent("city", "UNKNOWN");


        if (log.getIsSuspicious() == null) {
            log.setIsSuspicious(isSuspicious(log));
        }

        SecurityLog saved = repository.save(log);

        // CEF-лог только для подозрительных
        if (Boolean.TRUE.equals(saved.getIsSuspicious())) {
            Map<String, String> extension = new HashMap<>();
            extension.put("userId", String.valueOf(saved.getUserId()));
            extension.put("eventType", saved.getEventType());
            extension.put("ip", saved.getIpAddress());
            if (saved.getDeviceInfo() != null)
                extension.put("device_info", saved.getDeviceInfo());
            if (saved.getBiometryUsed() != null)
                extension.put("biometry", saved.getBiometryUsed().toString());
            extension.put("isSuspicious", String.valueOf(saved.getIsSuspicious()));
            extension.put("country", String.valueOf(saved.getMetadata().get("country")));
            extension.put("city", String.valueOf(saved.getMetadata().get("city")));

            String cefLog = CefUtil.toCef(
                    "1001",
                    saved.getEventType(),
                    8,
                    extension
            );
            cefLogger.info(cefLog);
        }
        return saved;
    }

    @Override
    public boolean isNewIp(Long userId, String currentIp) {
        if (currentIp == null || userId == null) return false;
        return repository.findByUserId(userId)
                .stream()
                .noneMatch(log -> currentIp.equals(log.getIpAddress()));
    }

    @Override
    public boolean isNewGeo(Long userId, String currentGeoLocation) {
        if (currentGeoLocation == null || userId == null) return false;
        String[] parts = currentGeoLocation.split(",");
        if (parts.length < 2) return false;
        String country = parts[0].trim();
        String city = parts[1].trim();
        return repository.findByUserId(userId)
                .stream()
                .noneMatch(log -> {
                    Map<String, Object> md = log.getMetadata();
                    if (md == null) return false;
                    return country.equals(md.get("country")) && city.equals(md.get("city"));
                });
    }

    @Override
    public boolean isNewDevice(Long userId, String currentDeviceInfo) {
        if (currentDeviceInfo == null || userId == null) return false;
        return repository.findByUserId(userId)
                .stream()
                .noneMatch(log -> currentDeviceInfo.equals(log.getDeviceInfo()));
    }

    @Override
    public boolean hasTooManyFailedAttempts(Long userId) {
        if (userId == null) return false;
        List<SecurityLog> lastAttempts = repository.findTop3ByUserIdAndEventTypeOrderByCreatedAtDesc(userId, EVENT_LOGIN_ATTEMPT);
        if (lastAttempts.size() < BLOCK_LIMIT) return false;
        return lastAttempts.stream().allMatch(log -> Boolean.TRUE.equals(log.getIsSuspicious()));
    }

    @Override
    public boolean hasTooManyPasswordChanges(Long userId) {
        if (userId == null) return false;
        LocalDateTime dayAgo = LocalDateTime.now().minusDays(1);
        List<SecurityLog> logs =
                repository.findByUserIdAndEventTypeAndCreatedAtAfter(userId, EVENT_PASSWORD_CHANGE, dayAgo);
        return logs.size() > 2;
    }

    @Override
    public boolean isLoginWithoutBiometryWhereWasBiometryBefore(Long userId, SecurityLog log) {
        if (log == null || userId == null) return false;
        if (!EVENT_LOGIN.equals(log.getEventType()) || Boolean.TRUE.equals(log.getBiometryUsed())) return false;
        List<SecurityLog> logs = repository.findByUserIdAndBiometryUsed(userId, true);
        return !logs.isEmpty();
    }

    @Override
    public boolean isBlacklistedCountry(String geoLocation) {
        if (geoLocation == null || geoLocation.isBlank()) return false;
        String country = geoLocation.split(",")[0].trim().toUpperCase();
        return bannedCountryRepository.existsById(country);
    }

    @Override
    public boolean isUserAgentMismatch(Long userId, String currentDeviceInfo) {
        if (userId == null || currentDeviceInfo == null) return false;
        List<SecurityLog> logs = repository.findByUserId(userId);
        Set<String> agents = new HashSet<>();
        for (SecurityLog log : logs) {
            String di = log.getDeviceInfo();
            if (di != null) agents.add(di);
        }
        return agents.size() > 1 && !agents.contains(currentDeviceInfo);
    }

    @Override
    public List<SecurityLog> findSuspiciousLogs() {
        List<SecurityLog> allLogs = repository.findAll();
        List<SecurityLog> result = new ArrayList<>();
        for (SecurityLog log : allLogs) {
            if (isSuspicious(log)) result.add(log);
        }
        return result;
    }

    @Override
    public List<SecurityLog> getLastLoginAttempts(Long userId, int limit) {

        return repository.findTop3ByUserIdAndEventTypeOrderByCreatedAtDesc(userId, EVENT_LOGIN_ATTEMPT);
    }

    @Override
    public List<SecurityLog> findSuspiciousLogsByUserId(Long userId) {
        List<SecurityLog> userLogs = repository.findByUserId(userId);
        List<SecurityLog> suspicious = new ArrayList<>();
        for (SecurityLog log : userLogs) {
            if (isSuspicious(log)) suspicious.add(log);
        }
        return suspicious;
    }

    @Override
    public List<SecurityLog> findByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    private boolean isSuspicious(SecurityLog log) {
        if (log == null) return false;

        if (EVENT_LOGIN_ATTEMPT.equals(log.getEventType())) {

            return Boolean.TRUE.equals(log.getIsSuspicious());
        }

        Long userId = log.getUserId();
        if (userId == null) return false;

        //  Вход без биометрии если раньше была биометрия — подозрительно
        if (EVENT_LOGIN.equals(log.getEventType()) && !Boolean.TRUE.equals(log.getBiometryUsed())) {
            if (isLoginWithoutBiometryWhereWasBiometryBefore(userId, log)) {
                return true;
            }
        }

        Map<String, Object> md = log.getMetadata();
        String country = md != null && md.get("country") != null ? md.get("country").toString() : "";
        String city = md != null && md.get("city") != null ? md.get("city").toString() : "";
        String geoString = (country + "," + city).trim();

        String deviceInfo = log.getDeviceInfo();
        String ip = log.getIpAddress();

        String platform = md != null && md.get("platform") != null ? md.get("platform").toString() : "";
        String browser = md != null && md.get("browser") != null ? md.get("browser").toString() : "";

        //  Новый IP
        if (isNewIp(userId, ip)) return true;

        //  Новое гео
        if (isNewGeo(userId, geoString)) return true;

        //  Новый девайс
        if (isNewDevice(userId, deviceInfo)) return true;

        // Неизвестная связка platform+browser
        if ((!platform.isEmpty() || !browser.isEmpty())) {
            List<SecurityLog> logs = repository.findByUserId(userId);
            boolean isNewComb = logs.stream().noneMatch(lg -> {
                Map<String, Object> oMd = lg.getMetadata();
                if (oMd == null) return false;
                String oPlatform = oMd.get("platform") != null ? oMd.get("platform").toString() : "";
                String oBrowser = oMd.get("browser") != null ? oMd.get("browser").toString() : "";
                return platform.equals(oPlatform) && browser.equals(oBrowser);
            });
            if (isNewComb) return true;
        }

        //  Много неудачных попыток (например, 3 подряд)
        if (hasTooManyFailedAttempts(userId)) return true;

        // Частая смена пароля
        if (hasTooManyPasswordChanges(userId)) return true;

        //  Страна в черном списке
        if (isBlacklistedCountry(geoString)) return true;

        //  Мисматч User-Agent-а
        if (isUserAgentMismatch(userId, deviceInfo)) return true;

        // Если ничего подозрительного не нашли
        return false;
    }
}