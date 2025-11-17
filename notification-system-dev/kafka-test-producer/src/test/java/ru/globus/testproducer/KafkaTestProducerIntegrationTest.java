package ru.globus.testproducer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import ru.globus.testproducer.dto.NotificationDto;
import ru.globus.testproducer.dto.NotificationRuleDto;
import ru.globus.testproducer.util.TestGenerator;
import ru.globus.testproducer.util.TestNotificationListener;
import ru.globus.testproducer.util.TestNotificationRuleListener;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
public class KafkaTestProducerIntegrationTest {

    @Container
    private static final KafkaContainer KAFKA = new KafkaContainer(
            DockerImageName.parse("apache/kafka:latest"));

    private static TestGenerator testGenerator;

    private static final String TEST_URL = "/kafka-test-producer/api/v1";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestNotificationListener testNotificationListener;

    @Autowired
    private TestNotificationRuleListener testNotificationRuleListener;

    @BeforeAll
    public static void setup() {
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", KAFKA.getBootstrapServers());
        testGenerator = new TestGenerator();
     }

    @Test
    public void whenPostMappingNotification_thenNotificationSentToKafka() throws Exception {
        // Given
        NotificationDto notificationDto = testGenerator.generateNotificationDto();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<NotificationDto> request = new HttpEntity<>(notificationDto, headers);
        String baseUrl = "http://localhost:" + port;

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                 baseUrl + TEST_URL + "/notifications",
                request,
                String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());

        NotificationDto notificationDtoActual = testNotificationListener.getResponse();

        assertNotNull(notificationDtoActual);
        assertEquals(notificationDto.getNotificationId(), notificationDtoActual.getNotificationId());
        assertEquals(notificationDto.getClientId(), notificationDtoActual.getClientId());
        assertEquals(notificationDto.getMessage(), notificationDtoActual.getMessage());
    }

    @Test
    public void whenGetMappingNotification_thenNotificationSentToKafka() throws Exception {
        // Given
        NotificationDto notificationDto = testGenerator.generateNotificationDto();
        String baseUrl = "http://localhost:" + port;

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + TEST_URL + "/notifications", String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());

        NotificationDto notificationDtoActual = testNotificationListener.getResponse();

        assertNotNull(notificationDtoActual);
        assertEquals(notificationDto.getClientId(), notificationDtoActual.getClientId());
        assertEquals(notificationDto.getMessage(), notificationDtoActual.getMessage());
    }

    @Test
    public void whenPostMappingNotificationRule_thenNotificationRuleSentToKafka() throws Exception {
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        NotificationRuleDto notificationRuleDto = testGenerator.generateNotificationRuleDto();
        HttpEntity<NotificationRuleDto> request = new HttpEntity<>(notificationRuleDto, headers);
        String baseUrl = "http://localhost:" + port;

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + TEST_URL + "/notification-rule",
                request,
                String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());

        NotificationRuleDto notificationRuleDtoActual = testNotificationRuleListener.getResponse();
        assertNotNull(notificationRuleDtoActual);
        assertEquals(notificationRuleDto.getClientId(), notificationRuleDtoActual.getClientId());
        assertEquals(notificationRuleDto.getEmail(), notificationRuleDtoActual.getEmail());
        assertEquals(notificationRuleDto.getPhone(), notificationRuleDtoActual.getPhone());
        assertEquals(notificationRuleDto.getPreferNotificationChannels(),
                notificationRuleDtoActual.getPreferNotificationChannels());
    }

    @Test
    public void whenGetMappingNotificationRule_thenNotificationRuleSentToKafka() throws Exception {
        // Given
        NotificationRuleDto notificationRuleDto = testGenerator.generateNotificationRuleDto();
        String baseUrl = "http://localhost:" + port;

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + TEST_URL + "/notification-rule", String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());

        NotificationRuleDto notificationRuleDtoActual = testNotificationRuleListener.getResponse();
        assertNotNull(notificationRuleDtoActual);
        assertEquals(notificationRuleDto.getClientId(), notificationRuleDtoActual.getClientId());
        assertEquals(notificationRuleDto.getEmail(), notificationRuleDtoActual.getEmail());
        assertEquals(notificationRuleDto.getPhone(), notificationRuleDtoActual.getPhone());
        assertEquals(notificationRuleDto.getPreferNotificationChannels(),
                notificationRuleDtoActual.getPreferNotificationChannels());
    }
}
