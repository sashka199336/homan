package ru.globus.smsnotificationms.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SmsResponse(
        String status,
        @JsonProperty("status_code") Integer statusCode,
        @JsonProperty("sms") Map<String, SmsDataResponse> sms,
        double balance) {
}
