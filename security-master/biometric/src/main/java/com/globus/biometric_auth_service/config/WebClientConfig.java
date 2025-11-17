package com.globus.biometric_auth_service.config;

import com.globus.biometric_auth_service.integration.SessionTracingIntegrationsProperties;
import com.globus.biometric_auth_service.integration.Modul26IntegrationsProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({
        SessionTracingIntegrationsProperties.class,
        Modul26IntegrationsProperties.class
})
public class WebClientConfig {

    private final SessionTracingIntegrationsProperties sessionTracingIntegrationsProperties;
    private final Modul26IntegrationsProperties modul26IntegrationsProperties;

    @Bean
    public WebClient sessionTracingServiceWebClient() {
        TcpClient tcpClient = TcpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        sessionTracingIntegrationsProperties.getConnectTimeout())
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(
                            sessionTracingIntegrationsProperties.getReadTimeout(), TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(
                            sessionTracingIntegrationsProperties.getWriteTimeout(), TimeUnit.MILLISECONDS));
                });
        return WebClient.builder()
                .baseUrl(sessionTracingIntegrationsProperties.getUrl())
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .build();
    }

    @Bean
    public WebClient modul26ServiceWebClient() {
        TcpClient tcpClient = TcpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        modul26IntegrationsProperties.getConnectTimeout())
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(
                            modul26IntegrationsProperties.getReadTimeout(), TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(
                            modul26IntegrationsProperties.getWriteTimeout(), TimeUnit.MILLISECONDS));
                });
        return WebClient.builder()
                .baseUrl(modul26IntegrationsProperties.getUrl())
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .build();
    }
}