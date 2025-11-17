
package com.globus.biometric_auth_service.integration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "integrations.modul26")
public class Modul26IntegrationsProperties {
    private String url;
    private int connectTimeout;
    private int readTimeout;
    private int writeTimeout;
}