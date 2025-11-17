package ru.globus.smsnotificationms.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SmsDataResponse(
        String status,
        @JsonProperty("status_code") Integer statusCode,
        @JsonProperty("status_text") Integer statusText,
        @JsonProperty("sms_id") String smsId) {
}
