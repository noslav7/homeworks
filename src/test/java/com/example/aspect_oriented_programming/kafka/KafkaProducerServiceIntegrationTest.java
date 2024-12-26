package com.example.aspect_oriented_programming.kafka;

import com.example.aspect_oriented_programming.BaseIntegrationTest;
import com.example.aspect_oriented_programming.dto.KafkaMessageDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThatCode;

class KafkaProducerServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Test
    void givenMessage_whenSendToKafka_thenNoException() {
        KafkaMessageDTO messageDTO = new KafkaMessageDTO();
        messageDTO.setTitle("Test Kafka");
        messageDTO.setContent("Kafka integration test message");

        assertThatCode(() -> kafkaProducerService.sendMessage("test-topic", messageDTO))
                .doesNotThrowAnyException();
    }
}
