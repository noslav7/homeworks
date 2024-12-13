package com.example.aspect_oriented_programming.kafka;

import com.example.aspect_oriented_programming.dto.KafkaMessageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(String topic, KafkaMessageDTO messageDTO) {
        try {
            String message = objectMapper.writeValueAsString(messageDTO);
            logger.info("Отправлено сообщение: {}", message);
            kafkaTemplate.send(topic, message);
        } catch (Exception e) {
            logger.error("Ошибка сериализации сообщения: ", e);
        }
    }
}
