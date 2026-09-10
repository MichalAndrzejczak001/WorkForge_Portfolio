package com.workforge.jobservice;

import com.workforge.jobservice.api.dto.request.CreateJobRequest;
import com.workforge.jobservice.api.dto.response.JobResponse;
import com.workforge.jobservice.application.service.JobService;
import com.workforge.jobservice.domain.model.ExperienceLevel;
import com.workforge.jobservice.domain.model.JobOffer;
import com.workforge.jobservice.domain.model.JobStatus;
import com.workforge.jobservice.domain.model.WorkMode;
import com.workforge.jobservice.infrastructure.persistence.JobRepository;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class JobServiceApplicationTests {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @Container
    static ConfluentKafkaContainer kafka = new ConfluentKafkaContainer("confluentinc/cp-kafka:7.6.0");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private JobService jobService;

    @Autowired
    private JobRepository jobRepository;

    private KafkaConsumer<String, String> consumer;

    @BeforeEach
    void setUpConsumer() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-" + UUID.randomUUID());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(List.of("job.published", "job.expired"));
    }

    @AfterEach
    void tearDownConsumer() {
        consumer.close();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void publishJob_shouldSendRealKafkaEvent() {
        // GIVEN
        UUID recruiterId = UUID.randomUUID();
        CreateJobRequest request = CreateJobRequest.builder()
                .title("Integration Test Job")
                .location("Warszawa")
                .salaryMin(new BigDecimal("8000"))
                .salaryMax(new BigDecimal("12000"))
                .workMode(WorkMode.REMOTE)
                .experienceLevel(ExperienceLevel.JUNIOR)
                .build();

        JobResponse created = jobService.createJob(request, recruiterId);

        // WHEN
        jobService.publishJob(created.getId());

        // THEN
        ConsumerRecords<String, String> records = null;
        long start = System.currentTimeMillis();
        while ((records == null || records.isEmpty()) && System.currentTimeMillis() - start < 10000) {
            records = consumer.poll(Duration.ofMillis(500));
        }

        assertThat(records).isNotNull();
        assertThat(records.count()).isGreaterThan(0);
        assertThat(records.iterator().next().value()).contains(created.getId().toString());
    }

    @Test
    void expireOverdueJobs_shouldMarkJobsAsExpiredAndSendKafkaEvent() {
        // GIVEN
        JobOffer overdueJob = JobOffer.builder()
                .title("Overdue Test Job")
                .status(JobStatus.PUBLISHED)
                .recruiterId(UUID.randomUUID())
                .expiresAt(LocalDateTime.now().minusDays(1))
                .viewsCount(0L)
                .applicationsCount(0L)
                .build();
        JobOffer saved = jobRepository.save(overdueJob);

        // WHEN
        jobService.expireOverdueJobs();

        // THEN
        boolean found = false;
        long start = System.currentTimeMillis();
        while(!found && System.currentTimeMillis() - start < 10000) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
            for (ConsumerRecord<String, String> record : records) {
                if (record.topic().equals("job.expired") && record.value().contains(saved.getId().toString())) {
                    found = true;
                    break;
                }
            }
        }

        assertThat(found).isTrue();

        JobOffer reloaded = jobRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(JobStatus.EXPIRED);
    }
}


