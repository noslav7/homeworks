package com.example.aspect_oriented_programming.kafka;

import com.example.aspect_oriented_programming.dto.KafkaMessageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

class KafkaProducerServiceTest {

    @Test
    void givenValidKafkaMessage_whenSendMessage_thenMessageIsSentSuccessfully() {
        KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);
        ObjectMapper objectMapper = new ObjectMapper();
        KafkaProducerService kafkaProducerService = new KafkaProducerService(kafkaTemplate, objectMapper);

        KafkaMessageDTO messageDTO = new KafkaMessageDTO();
        messageDTO.setTitle("Test Title");
        messageDTO.setContent("Test Content");

        kafkaProducerService.sendMessage("test-topic", messageDTO);

        verify(kafkaTemplate, times(1)).send(eq("test-topic"), any(String.class));
    }

    @Test
    void givenSerializationErrorOccurs_whenSendMessage_thenErrorIsLogged() {
        KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        KafkaProducerService kafkaProducerService = new KafkaProducerService(kafkaTemplate, objectMapper);

        KafkaMessageDTO messageDTO = new KafkaMessageDTO();
        messageDTO.setTitle("Test Title");
        messageDTO.setContent("Test Content");

        try {
            doThrow(new RuntimeException("Serialization error")).when(objectMapper).writeValueAsString(any(KafkaMessageDTO.class));
        } catch (Exception e) {
            throw new RuntimeException("Mock configuration failed", e);
        }

        kafkaProducerService.sendMessage("test-topic", messageDTO);

        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }
}
