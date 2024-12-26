package com.example.aspect_oriented_programming.service;

import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Test
    void givenValidEmailDetails_whenSendNotification_thenEmailIsSentSuccessfully() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        NotificationService notificationService = new NotificationService(mailSender);

        notificationService.sendNotification("test@example.com", "Test Subject", "Test Message");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void givenMailSenderThrowsException_whenSendNotification_thenExceptionIsHandled() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        doThrow(new RuntimeException("Mail error")).when(mailSender).send(any(SimpleMailMessage.class));
        NotificationService notificationService = new NotificationService(mailSender);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                notificationService.sendNotification(
                        "test@example.com", "Test Subject", "Test Message"));

        assertEquals("Mail error", exception.getMessage());
    }
}
