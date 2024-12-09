package com.example.aspect_oriented_programming.kafka;

import com.example.aspect_oriented_programming.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final NotificationService notificationService;

    public KafkaConsumerService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "test-topic", groupId = "task-service")
    private void listen(String message) {
        logger.info("Получено сообщение: {}", message);
        notificationService.sendNotification("noslav7@gmail.com", "Task Update", message);
    }
}
