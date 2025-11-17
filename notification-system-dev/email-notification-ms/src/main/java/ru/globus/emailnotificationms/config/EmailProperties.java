package ru.globus.emailnotificationms.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.HashMap;

@ConfigurationProperties(prefix = "email")
@Getter
@Setter
@Component
public class EmailProperties {

    private HashMap<String, String> mailProperties;
}
