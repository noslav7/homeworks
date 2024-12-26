package com.example.aspect_oriented_programming.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@EnableKafka
class KafkaConfigTest {

    @Test
    void givenKafkaConfig_whenConsumerFactoryIsCreated_thenConfigIsCorrect() {
        KafkaConfig kafkaConfig = new KafkaConfig();
        ConsumerFactory<String, String> consumerFactory = kafkaConfig.consumerFactory();

        assertTrue(consumerFactory instanceof DefaultKafkaConsumerFactory);

        Map<String, Object> configs = consumerFactory.getConfigurationProperties();
        assertEquals("localhost:9092", configs.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals("task-service", configs.get(ConsumerConfig.GROUP_ID_CONFIG));
        assertEquals(StringDeserializer.class, configs.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
        assertEquals(StringDeserializer.class, configs.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
        assertEquals(false, configs.get(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG));
        assertEquals(10, configs.get(ConsumerConfig.MAX_POLL_RECORDS_CONFIG));
    }

    @Test
    void givenKafkaConfig_whenKafkaBatchListenerContainerFactoryIsCreated_thenConfigIsCorrect() {
        KafkaConfig kafkaConfig = new KafkaConfig();

        ConsumerFactory<String, String> consumerFactory = mock(ConsumerFactory.class);

        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                kafkaConfig.kafkaBatchListenerContainerFactory(consumerFactory);

        assertEquals(consumerFactory, factory.getConsumerFactory());

        assertTrue(factory.isBatchListener());

        assertEquals(
                org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL,
                factory.getContainerProperties().getAckMode()
        );
    }
}
