package ru.globus.smsnotificationms.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.globus.smsnotificationms.config.FeignConfig;
import ru.globus.smsnotificationms.dto.response.SmsResponse;

@FeignClient(
        name = "smsService",
        url = "#{@smsProperties.getSmsProperties().get('url')}",
        configuration = FeignConfig.class
)
public interface SmsServiceClient {

    @GetMapping(
            path = "/sms/send",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_HTML_VALUE}
    )
    SmsResponse sendSms(
            @RequestParam("api_id") String apiId,
            @RequestParam("to") String to,
            @RequestParam("msg") String msg,
            @RequestParam(value = "json", defaultValue = "1") String json,
            @RequestParam(value = "test", defaultValue = "0") String test
    );
}

