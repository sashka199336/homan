package com.globus.payment_service;

import com.globus.payment_service.dto.NeedForPaymentEventFromLoanSystem;
import com.globus.payment_service.outbox.OutboxMasterScheduler;
import com.globus.payment_service.outbox.repository.OutboxPhsRepository;
import com.globus.payment_service.repository.TransactionHistoryRepository;
import com.globus.payment_service.service.impl.TransactionServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

@SpringBootTest
@DirtiesContext
@ExtendWith(MockitoExtension.class)
@Testcontainers
@Transactional
@ActiveProfiles("test")
public class AbstractTest {

    @Autowired
    protected TransactionServiceImpl transactionService;
    @Autowired
    protected TransactionHistoryRepository transactionHistoryRepository;
    @Autowired
    protected OutboxPhsRepository outboxRepository;
    @Autowired
    protected OutboxMasterScheduler masterScheduler;

    @PersistenceContext
    protected EntityManager entityManager;

    protected final static PrintStream standardOut = System.out;
    protected final static ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    protected static PostgreSQLContainer postgreSQLContainer;
    static {
        try {
            DockerImageName postgres = DockerImageName.parse("postgres");
            postgreSQLContainer = (PostgreSQLContainer) new PostgreSQLContainer(postgres)
                    .withExposedPorts(5432)
                    .withEnv("POSTGRES_USER", "postgres")
                    .withEnv("POSTGRES_PASSWORD", "postgres")
                    .withEnv("POSTGRES_DB", "glb_1_23")
                    .withReuse(true);
            postgreSQLContainer.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Container
    static final KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("apache/kafka"));

    @DynamicPropertySource
    public static void registerProperties (DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.kafka.bootstrap_servers", kafkaContainer::getBootstrapServers);
    }


    protected static UUID transactionId;
    protected static NeedForPaymentEventFromLoanSystem fullMessageFromLoan = NeedForPaymentEventFromLoanSystem.builder()
            .claimId(UUID.fromString("4d3669de-4a72-4d92-8709-3bf339a8c762"))
            .amount(BigDecimal.valueOf(200000))
            .toBill("40817810100001234505")
            .build();

    @BeforeAll
    public static void init() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        try (AdminClient adminClient = AdminClient.create(props)) {
            List<NewTopic> topics = Arrays.asList(
                    new NewTopic("payment.account.direct-events", 1, (short) 1),
                    new NewTopic("loan.payment.direct-events", 1, (short) 1),
                    new NewTopic("account.payment.direct-events", 1, (short) 1),
                    new NewTopic("payment.claim.direct-events", 1, (short) 1)
            );
            adminClient.createTopics(topics).all().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
