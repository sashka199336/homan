package ru.globus.emailnotificationms;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import ru.globus.emailnotificationms.dto.request.EmailNotificationDto;
import ru.globus.emailnotificationms.dto.request.SenderDto;
import ru.globus.emailnotificationms.dto.response.NotificationResponse;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class EmailIntegrationTest {

    private static final String EMAIL_NOTIFICATION_SYSTEM = "email-notifications-topic";
    private static final int TIMEOUT_SECONDS = 10;
    @Container
    private static final KafkaContainer KAFKA = new KafkaContainer(
            DockerImageName.parse("apache/kafka:latest"));

    @Container
    private static final GenericContainer greenMailGenericContainer = new GenericContainer<>(DockerImageName.parse("greenmail/standalone:latest"))
            .waitingFor(Wait.forLogMessage(".*Starting GreenMail standalone.*", 1))
            .withEnv("GREENMAIL_OPTS", "-Dgreenmail.setup.test.smtp -Dgreenmail.hostname=0.0.0.0 -Dgreenmail.auth.disabled -Dgreenmail.users=test:password")
            .withExposedPorts(3025);

    @Autowired
    private KafkaTemplate<String, EmailNotificationDto> kafkaTemplateTestProducer;

    @Autowired
    private TestResponseListener testResponseListener;

    private EmailNotificationDto notificationDto;

    @BeforeAll
    public static void setup() {
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", KAFKA.getBootstrapServers());
        System.setProperty("GREEN_MAIL_HOST", greenMailGenericContainer.getHost());
        System.setProperty("GREEN_MAIL_PORT", String.valueOf(greenMailGenericContainer.getFirstMappedPort()));
    }

    @BeforeEach
    public void setUp() {
        notificationDto = new EmailNotificationDto();
        notificationDto.setNotificationId(UUID.randomUUID().toString());
        notificationDto.setCreatedAt(LocalDateTime.now());

        SenderDto sender = new SenderDto();
        sender.setSystem("Test System");
        sender.setUserId("user123");
        notificationDto.setSender(sender);

        notificationDto.setMessage("Test message");
        notificationDto.setEmail("test@test.com");
    }

    @Test
    public void whenSendEmailNotification_thenResponseSentToKafka() throws Exception {
        // When
        kafkaTemplateTestProducer.send(EMAIL_NOTIFICATION_SYSTEM,
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
        notificationDto.setEmail(null);

        // When
        kafkaTemplateTestProducer.send(EMAIL_NOTIFICATION_SYSTEM,
                        notificationDto.getNotificationId(),
                        notificationDto)
                .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        // Then
        NotificationResponse notificationResponse = testResponseListener.getResponse();

            assertNotNull(notificationResponse);
            assertEquals(notificationDto.getNotificationId(), notificationResponse.getNotificationId());
            assertEquals("EMAIL", notificationResponse.getChannel());
            assertEquals("ERROR", notificationResponse.getStatus());
            assertNotNull(notificationResponse.getErrorCode());
            assertNotNull(notificationResponse.getErrorMessage());
    }
}

