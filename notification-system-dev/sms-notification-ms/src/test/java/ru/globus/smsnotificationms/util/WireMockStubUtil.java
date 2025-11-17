package ru.globus.smsnotificationms.util;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.stereotype.Component;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;

@Component
public class WireMockStubUtil {

    public static void setupSuccessStub(TestGenerator testGenerator, String phone) {
        setupStub(testGenerator.generateUrlWithParameters(phone),
                testGenerator.generateSmsResponseOk(),
                200);
    }

    public static void setupErrorStub(TestGenerator testGenerator, String phone) {
        setupStub(testGenerator.generateUrlWithParameters(phone),
                testGenerator.generateSmsResponseError(),
                400);
    }

    private static void setupStub(String url, String responseBody, int status) {
        MappingBuilder mappingBuilder = WireMock.get(url);
        ResponseDefinitionBuilder responseBuilder = aResponse()
                .withStatus(status)
                .withHeader("Content-Type", "application/json")
                .withBody(responseBody);

        stubFor(mappingBuilder.willReturn(responseBuilder));
    }
}
