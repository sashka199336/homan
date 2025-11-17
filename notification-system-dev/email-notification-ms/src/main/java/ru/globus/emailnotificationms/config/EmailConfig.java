package ru.globus.emailnotificationms.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import java.util.HashMap;
import java.util.Properties;

@Slf4j
@Configuration
@EnableConfigurationProperties(EmailProperties.class)
@RequiredArgsConstructor
public class EmailConfig {

    private final EmailProperties emailProperties;

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        HashMap<String, String> mailProperties = emailProperties.getMailProperties();
        mailSender.setHost(mailProperties.get("host"));
        mailSender.setPort(Integer.parseInt(mailProperties.get("port")));
        mailSender.setUsername(mailProperties.get("username"));
        mailSender.setPassword(mailProperties.get("password"));

        Properties props = new Properties();
        props.put("mail.transport.protocol", mailProperties.get("transport.protocol"));
        props.put("mail.smtp.auth", mailProperties.get("smtp.auth"));
        props.put("mail.smtp.ssl.enable", mailProperties.get("smtp.ssl.enable"));
        props.put("mail.debug", mailProperties.get("debug"));
        mailSender.setJavaMailProperties(props);

        return mailSender;
    }

    @Bean
    public SimpleMailMessage templateMessage() {
        return new SimpleMailMessage();
    }
}
