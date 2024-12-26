package com.example.aspect_oriented_programming.kafka;

import com.example.aspect_oriented_programming.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

import static org.mockito.Mockito.*;

class KafkaConsumerServiceTest {

    @Test
    void givenValidKafkaMessages_whenListen_thenMessagesAreProcessedSuccessfully() {
        NotificationService notificationService = mock(NotificationService.class);
        ObjectMapper objectMapper = new ObjectMapper();
        KafkaConsumerService kafkaConsumerService = new KafkaConsumerService(notificationService, objectMapper);

        ConsumerRecord<String, String> record = mock(ConsumerRecord.class);
        when(record.value()).thenReturn("{\"title\":\"Test Title\",\"content\":\"Test Content\"}");

        Acknowledgment acknowledgment = mock(Acknowledgment.class);

        kafkaConsumerService.listen(List.of(record), acknowledgment);

        verify(notificationService, times(1)).sendNotification("noslav7@gmail.com", "Test Title", "Test Content");
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void givenInvalidKafkaMessage_whenListen_thenExceptionIsHandled() {
        NotificationService notificationService = mock(NotificationService.class);
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        KafkaConsumerService kafkaConsumerService = new KafkaConsumerService(notificationService, objectMapper);

        ConsumerRecord<String, String> record = mock(ConsumerRecord.class);
        when(record.value()).thenReturn("Invalid JSON");

        Acknowledgment acknowledgment = mock(Acknowledgment.class);

        try {
            kafkaConsumerService.listen(List.of(record), acknowledgment);
        } catch (Exception e) {
            verify(notificationService, never()).sendNotification(anyString(), anyString(), anyString());
            verify(acknowledgment, never()).acknowledge();
        }
    }
}
