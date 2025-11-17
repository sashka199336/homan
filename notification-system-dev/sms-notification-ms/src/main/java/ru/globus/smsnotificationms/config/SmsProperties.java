package ru.globus.smsnotificationms.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.HashMap;

@ConfigurationProperties(prefix = "sms")
@Getter
@Setter
@Component
public class SmsProperties {

    private HashMap<String, String> smsProperties;
}
