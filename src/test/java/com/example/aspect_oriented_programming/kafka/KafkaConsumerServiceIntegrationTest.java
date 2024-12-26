package com.example.aspect_oriented_programming.kafka;

import com.example.aspect_oriented_programming.BaseIntegrationTest;
import com.example.aspect_oriented_programming.dto.KafkaMessageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaConsumerServiceIntegrationTest extends BaseIntegrationTest {

    private static final String TOPIC = "test-topic";
    private KafkaProducer<String, String> producer;
    private KafkaConsumer<String, String> consumer;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUpKafka() {
        KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));
        kafkaContainer.start();

        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producer = new KafkaProducer<>(producerProps);

        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-service");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(TOPIC));
    }

    @Test
    void testKafkaConsumerReceivesMessages() throws JsonProcessingException {
        KafkaMessageDTO messageDTO = new KafkaMessageDTO();
        messageDTO.setTitle("Test Title");
        messageDTO.setContent("Test Content");
        String serializedMessage = objectMapper.writeValueAsString(messageDTO);

        producer.send(new ProducerRecord<>(TOPIC, serializedMessage));

        ConsumerRecord<String, String> record = consumer.poll(Duration.ofSeconds(5)).iterator().next();
        assertThat(record).isNotNull();

        KafkaMessageDTO receivedMessage = objectMapper.readValue(record.value(), KafkaMessageDTO.class);
        assertThat(receivedMessage.getTitle()).isEqualTo("Test Title");
        assertThat(receivedMessage.getContent()).isEqualTo("Test Content");
    }
}
