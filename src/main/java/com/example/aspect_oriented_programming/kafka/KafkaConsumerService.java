package com.example.aspect_oriented_programming.kafka;

import com.example.aspect_oriented_programming.dto.KafkaMessageDTO;
import com.example.aspect_oriented_programming.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaConsumerService {
    private final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public KafkaConsumerService(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "test-topic", groupId = "task-service", containerFactory = "kafkaBatchListenerContainerFactory")
    private void listen(List<ConsumerRecord<String, String>> records, Acknowledgment acknowledgment) {
        logger.info("Получено {} сообщений", records.size());

        try {
            for (ConsumerRecord<String, String> record : records) {
                KafkaMessageDTO messageDTO = objectMapper.readValue(record.value(), KafkaMessageDTO.class);
                logger.info("Обработано сообщение: {}", messageDTO);

                notificationService.sendNotification("noslav7@gmail.com", messageDTO.getTitle(), messageDTO.getContent());
            }
            acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Ошибка обработки сообщений: ", e);
        }
    }
}

