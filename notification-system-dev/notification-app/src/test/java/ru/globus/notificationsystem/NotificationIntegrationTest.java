package ru.globus.notificationsystem;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import ru.globus.notificationsystem.dto.request.*;
import ru.globus.notificationsystem.dto.response.NotificationResponse;
import ru.globus.notificationsystem.entity.Channel;
import ru.globus.notificationsystem.entity.NotificationHistory;
import ru.globus.notificationsystem.entity.NotificationRule;
import ru.globus.notificationsystem.entity.Status;
import ru.globus.notificationsystem.kafka.consumer.TestEmailNotificationListener;
import ru.globus.notificationsystem.kafka.consumer.TestSmsNotificationListener;
import ru.globus.notificationsystem.repository.NotificationHistoryRepository;
import ru.globus.notificationsystem.repository.NotificationRuleRepository;
import ru.globus.notificationsystem.util.TestGenerator;
import java.util.Optional;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NotificationIntegrationTest {

    private static final String NOTIFICATIONS_TOPIC = "notifications-topic";
    private static final String NOTIFICATION_RULE_TOPIC = "notifications-rule-topic";
    private static final String NOTIFICATION_RESPONSES_TOPIC = "notification-responses-topic";

    @Container
    private static final KafkaContainer KAFKA = new KafkaContainer(
            DockerImageName.parse("apache/kafka:latest"));

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test_notification_db")
            .withUsername("testuser")
            .withPassword("testpass");

    private static TestGenerator testGenerator;
    private static NotificationRule notificationRuleExpected;

    @Autowired
    private KafkaTemplate<String, NotificationDto> notificationDtoTestProducer;

    @Autowired
    private KafkaTemplate<String, NotificationRuleDto> notificationRuleDtoTestProducer;

    @Autowired
    private KafkaTemplate<String, NotificationResponse> notificationResponseTestProducer;

    @Autowired
    private TestEmailNotificationListener testEmailNotificationListener;

    @Autowired
    private TestSmsNotificationListener testSmsNotificationListener;

    @Autowired
    private NotificationRuleRepository notificationRuleRepository;

    @Autowired
    private NotificationHistoryRepository notificationHistoryRepository;

    private NotificationDto notificationDto;
    private NotificationDto notificationDtoNullClient;
    private EmailNotificationDto emailExpected;
    private SmsNotificationDto smsExpected;
    private NotificationRule notificationRule;

    @BeforeAll
    public static void setup() {
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", KAFKA.getBootstrapServers());
        testGenerator = new TestGenerator();
        notificationRuleExpected = testGenerator.generateNotificationRule();
    }

    @BeforeEach
    public void setUp() {
        notificationRule = testGenerator.generateNotificationRule();
        notificationDto = testGenerator.generateNotificationDto();
        notificationDtoNullClient = testGenerator.generateNotificationDtoWithNullClient();
        emailExpected = testGenerator.generateEmailNotificationDtoExpected();
        smsExpected = testGenerator.generateSmsNotificationDtoExpected();
    }

    @AfterEach
    public void tearDown() {
        notificationHistoryRepository.deleteAll();
        notificationHistoryRepository.flush();
        notificationRuleRepository.deleteAll();
        notificationRuleRepository.flush();
    }

    @Test
    @Order(2)
    void whenSendNotification_thenChannelNotificationSentToKafkaAndHistorySaveToDatabase() throws InterruptedException {
        //Given
        notificationRuleRepository.save(notificationRule);
        notificationDtoTestProducer.send(NOTIFICATIONS_TOPIC,
                            notificationDto.getNotificationId(),
                            notificationDto);

        //When
        EmailNotificationDto emailActual = testEmailNotificationListener.getResponse();
        SmsNotificationDto smsActual = testSmsNotificationListener.getResponse();
        NotificationHistory emailHistoryActual = notificationHistoryRepository
                .findByNotificationIdAndChannel(
                        notificationDto.getNotificationId(), Channel.EMAIL)
                .orElseThrow(() -> new AssertionError("Email NotificationHistory not found in database"));
        NotificationHistory smsHistoryActual = notificationHistoryRepository
                .findByNotificationIdAndChannel(
                        notificationDto.getNotificationId(), Channel.SMS)
                .orElseThrow(() -> new AssertionError("Sms NotificationHistory not found in database"));

        //Then
        assertEquals(emailExpected.getNotificationId(), emailActual.getNotificationId());
        assertEquals(emailExpected.getEmail(), emailActual.getEmail());
        assertEquals(emailExpected.getSender(), emailActual.getSender());
        assertEquals(emailExpected.getMessage(), emailActual.getMessage());

        assertEquals(smsExpected.getNotificationId(), smsActual.getNotificationId());
        assertEquals(smsExpected.getPhone(), smsActual.getPhone());
        assertEquals(smsExpected.getSender(), smsActual.getSender());
        assertEquals(smsExpected.getMessage(), smsActual.getMessage());

        assertEquals(Status.IN_PROGRESS, emailHistoryActual.getStatus());
        assertEquals(Status.IN_PROGRESS, smsHistoryActual.getStatus());
    }

    @Test
    @Order(3)
    void whenSendInvalidNotification_thenHistoryDoNotSaveToDatabase() {
        //Given
        notificationRuleRepository.save(notificationRule);
        notificationDtoTestProducer.send(NOTIFICATIONS_TOPIC,
                            notificationDtoNullClient.getNotificationId(),
                            notificationDtoNullClient);

        //When
        Optional<NotificationHistory> emailHistoryActual = notificationHistoryRepository
                    .findByNotificationIdAndChannel(
                            notificationDto.getClientId(), Channel.EMAIL);
        Optional<NotificationHistory> smsHistoryActual = notificationHistoryRepository
                .findByNotificationIdAndChannel(
                        notificationDto.getClientId(), Channel.EMAIL);

        //Then
        assertTrue(emailHistoryActual.isEmpty(), "Email NotificationHistory should not exist in database");
        assertTrue(smsHistoryActual.isEmpty(), "Sms NotificationHistory should not exist in database");

    }

    @Test
    @Order(1)
    public void whenSendNotificationRuleDto_thenNotificationRuleSaveToDatabase() {
        // Given
        NotificationRuleDto notificationRuleDtoValid = testGenerator.generateNotificationRuleDtoValid();

        // When
        notificationRuleDtoTestProducer.send(NOTIFICATION_RULE_TOPIC,
                notificationRuleDtoValid.getClientId(),
                notificationRuleDtoValid);

        // Then
        NotificationRule notificationRuleActual = await()
                .atMost(15, SECONDS)
                .until(
                        () -> notificationRuleRepository
                                .findByClientId(notificationRuleDtoValid.getClientId()),
                        Optional::isPresent
                ).orElseThrow();

        await().atMost(15, SECONDS)
                .untilAsserted(() -> {
                    Optional<NotificationRule> result = notificationRuleRepository.findByClientId(notificationRuleDtoValid.getClientId());

                    assertTrue(result.isPresent(), "NotificationRule should be saved for data");
                });
    }

    @Test
    @Order(4)
    public void whenSendValidNotificationResponse_thenNotificationHistoryStatusUpdated() {
        // Given
        NotificationHistory notificationHistoryInit = testGenerator.generateNotificationHistoryEmail();
        notificationHistoryRepository.save(notificationHistoryInit);
        NotificationResponse notificationResponse = testGenerator.generateValidNotificationResponse();

        // When
        notificationResponseTestProducer.send(NOTIFICATION_RESPONSES_TOPIC,
                notificationResponse.getNotificationId(),
                notificationResponse);

        // Then
        NotificationHistory notificationHistoryUpdated = await()
                .atMost(15, SECONDS)
                .pollInterval(200, MILLISECONDS)
                .until(
                        () -> notificationHistoryRepository
                                .findByNotificationIdAndChannel(
                                        notificationHistoryInit.getNotificationId(),
                                        notificationHistoryInit.getChannel()),
                        Optional::isPresent
                ).orElseThrow();

        assertEquals(Status.SUCCESS, notificationHistoryUpdated.getStatus());
    }

    @Test
    @Order(5)
    public void whenSendInvalidNotificationResponse_thenNotificationHistoryStatusUpdateError() {
        // Given
        NotificationHistory notificationHistoryInit = testGenerator.generateNotificationHistorySms();
        notificationHistoryRepository.save(notificationHistoryInit);
        NotificationResponse notificationResponse = testGenerator.generateInvalidNotificationResponse();

        // When
        notificationResponseTestProducer.send(NOTIFICATION_RESPONSES_TOPIC,
                notificationResponse.getNotificationId(),
                notificationResponse);

        // Then
        NotificationHistory notificationHistoryUpdated = await()
                .atMost(15, SECONDS)
                .pollInterval(200, MILLISECONDS)
                .until(
                        () -> notificationHistoryRepository
                                .findByNotificationIdAndChannel(
                                        notificationHistoryInit.getNotificationId(),
                                        notificationHistoryInit.getChannel()),
                        Optional::isPresent
                ).orElseThrow();

        assertEquals(Status.ERROR, notificationHistoryUpdated.getStatus());
    }
}
