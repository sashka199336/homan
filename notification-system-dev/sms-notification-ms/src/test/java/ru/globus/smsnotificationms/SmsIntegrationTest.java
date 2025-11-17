package ru.globus.smsnotificationms;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import ru.globus.smsnotificationms.dto.request.SmsNotificationDto;
import ru.globus.smsnotificationms.dto.response.NotificationResponse;
import ru.globus.smsnotificationms.util.TestGenerator;
import ru.globus.smsnotificationms.util.TestResponseListener;
import ru.globus.smsnotificationms.util.WireMockStubUtil;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers
@AutoConfigureWireMock(port = 8888)
@ActiveProfiles("test")
public class SmsIntegrationTest {

    private static final String SMS_NOTIFICATIONS_TOPIC = "sms-notifications-topic";
    private static final int TIMEOUT_SECONDS = 10;

    @Container
    private static final KafkaContainer KAFKA = new KafkaContainer(
            DockerImageName.parse("apache/kafka:latest"));

    private static TestGenerator testGenerator;

    @Autowired
    private KafkaTemplate<String, SmsNotificationDto> kafkaTemplateTestProducer;

    @Autowired
    private TestResponseListener testResponseListener;

    @BeforeAll
    public static void setup() {
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", KAFKA.getBootstrapServers());
        testGenerator = new TestGenerator();
    }

    @Test
    public void whenSendEmailNotification_thenResponseSentToKafka() throws Exception {
        // Given
        SmsNotificationDto notificationDto = testGenerator.generateNotificationDto();
        WireMockStubUtil.setupSuccessStub(testGenerator, notificationDto.getPhone());

        // When
        kafkaTemplateTestProducer.send(SMS_NOTIFICATIONS_TOPIC,
                        notificationDto.getNotificationId(),
                        notificationDto)
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        // Then
        NotificationResponse response = testResponseListener.getResponse();
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
    }

    @Test
    public void whenSendInvalidEmailNotification_thenErrorResponseSentToKafka() throws Exception {
        // Given
        SmsNotificationDto notificationDto = testGenerator.generateNotificationDto();
        notificationDto.setPhone(null);
        WireMockStubUtil.setupErrorStub(testGenerator, notificationDto.getPhone());

        // When
        kafkaTemplateTestProducer.send(SMS_NOTIFICATIONS_TOPIC,
                        notificationDto.getNotificationId(),
                        notificationDto)
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        // Then
        NotificationResponse notificationResponse = testResponseListener.getResponse();

        assertNotNull(notificationResponse);
        assertEquals(notificationDto.getNotificationId(), notificationResponse.getNotificationId());
        assertEquals("SMS", notificationResponse.getChannel());
        assertEquals("ERROR", notificationResponse.getStatus());
        assertNotNull(notificationResponse.getErrorCode());
        assertNotNull(notificationResponse.getErrorMessage());
    }
}
