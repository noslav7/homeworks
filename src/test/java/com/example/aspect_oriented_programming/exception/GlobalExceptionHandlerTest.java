package com.example.aspect_oriented_programming.exception;

import com.example.aspect_oriented_programming.controller.TaskController;
import com.example.aspect_oriented_programming.service.TaskService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.KafkaException;
import org.springframework.mail.MailException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    @DisplayName("Given HttpMessageNotReadableException, Then return 400 Bad Request with error message")
    void givenHttpMessageNotReadableException_whenGetTask_thenBadRequest() throws Exception {
        Mockito.when(taskService.getTaskById(anyLong()))
                .thenThrow(new org.springframework.http.converter.HttpMessageNotReadableException("Некорректный формат"));

        mockMvc.perform(get("/tasks/{id}", 15L))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Некорректный формат запроса: Некорректный формат"));
    }

    @Nested
    @DisplayName("NoSuchElementException handling")
    class NoSuchElementExceptionTests {
        @Test
        @DisplayName("Given taskService throws NoSuchElementException, Then return 404 NotFound")
        void givenNoSuchElementException_whenGetTask_thenNotFound() throws Exception {
            Mockito.when(taskService.getTaskById(anyLong()))
                    .thenThrow(new NoSuchElementException("Элемент не найден"));

            mockMvc.perform(get("/tasks/{id}", 123L))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Элемент не найден"));
        }
    }

    @Test
    @DisplayName("Given ConstraintViolationException, Then return 400 Bad Request with detail message")
    void givenConstraintViolationException_whenGetTask_thenBadRequest() throws Exception {
        ConstraintViolation<?> mockViolation = Mockito.mock(ConstraintViolation.class);
        Path mockPath = Mockito.mock(Path.class);

        Mockito.when(mockPath.toString()).thenReturn("title");

        Mockito.when(mockViolation.getPropertyPath()).thenReturn(mockPath);
        Mockito.when(mockViolation.getMessage()).thenReturn("не может быть пустым");

        ConstraintViolationException cve = new ConstraintViolationException(
                "Валидационная ошибка",
                Set.of(mockViolation)
        );

        Mockito.when(taskService.getTaskById(anyLong())).thenThrow(cve);

        mockMvc.perform(get("/tasks/{id}", 123L))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("Валидационная ошибка: [title не может быть пустым]")
                ));
    }


    @Test
    @DisplayName("Given DataIntegrityViolationException, Then return 409 Conflict with cause message")
    void givenDataIntegrityViolationException_whenGetTask_thenConflict() throws Exception {
        DataIntegrityViolationException dive = new DataIntegrityViolationException(
                "Дублирующее значение ключа нарушает уникальное ограничение"
        );

        Mockito.when(taskService.getTaskById(anyLong())).thenThrow(dive);

        mockMvc.perform(get("/tasks/{id}", 123L))
                .andExpect(status().isConflict())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "Нарушение целостности данных: " +
                                        "Дублирующее значение ключа нарушает уникальное ограничение")
                ));
    }

    @Test
    @DisplayName("Given DataIntegrityViolationException without root cause, Then return 409 Conflict")
    void givenDataIntegrityViolationExceptionWithoutRootCause_whenGetTask_thenConflict() throws Exception {
        DataIntegrityViolationException dive = new DataIntegrityViolationException("Основное сообщение ошибки");

        Mockito.when(taskService.getTaskById(anyLong())).thenThrow(dive);

        mockMvc.perform(get("/tasks/{id}", 16L))
                .andExpect(status().isConflict())
                .andExpect(content().string("Нарушение целостности данных: Основное сообщение ошибки"));
    }

    @Nested
    @DisplayName("KafkaException handling")
    class KafkaExceptionTests {
        @Test
        @DisplayName("Given service throws KafkaException, Then return 503 Service Unavailable")
        void givenKafkaException_whenGetTask_thenServiceUnavailable() throws Exception {
            Mockito.when(taskService.getTaskById(anyLong()))
                    .thenThrow(new KafkaException("Kafka error"));

            mockMvc.perform(get("/tasks/{id}", 10L))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(content().string("Ошибка Kafka: Kafka error"));
        }
    }

    @Nested
    @DisplayName("MailException handling")
    class MailExceptionTests {
        @Test
        @DisplayName("Given service throws MailException, Then return 500 Internal Server Error")
        void givenMailException_whenGetTask_thenInternalServerError() throws Exception {
            Mockito.when(taskService.getTaskById(anyLong()))
                    .thenThrow(new MailException("Mail error") {
                    });

            mockMvc.perform(get("/tasks/{id}", 11L))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("Ошибка отправки почты: Mail error"));
        }
    }

    @Nested
    @DisplayName("ConstraintViolationException handling")
    class ConstraintViolationExceptionTests {
        @Test
        @DisplayName("Given service throws ConstraintViolationException, Then return 400 Bad Request")
        void givenConstraintViolationException_whenGetTask_thenBadRequest() throws Exception {
            Mockito.when(taskService.getTaskById(anyLong()))
                    .thenThrow(new ConstraintViolationException("Валидационная ошибка", Set.of()));

            mockMvc.perform(get("/tasks/{id}", 12L))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Валидационная ошибка: "));
        }
    }

    @Nested
    @DisplayName("RuntimeException handling")
    class RuntimeExceptionTests {
        @Test
        @DisplayName("Given service throws RuntimeException, Then return 500 InternalServerError")
        void givenRuntimeException_whenGetTask_thenInternalServerError() throws Exception {
            Mockito.when(taskService.getTaskById(anyLong()))
                    .thenThrow(new RuntimeException("Runtime error"));

            mockMvc.perform(get("/tasks/{id}", 13L))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("Ошибка сервера: Runtime error"));
        }
    }

    @Nested
    @DisplayName("Any other Exception handling")
    class ExceptionTests {
        @Test
        @DisplayName("Given service throws any Exception, Then return 500 with message")
        void givenException_whenGetTask_thenInternalServerError() throws Exception {
            Mockito.when(taskService.getTaskById(anyLong()))
                    .thenThrow(new RuntimeException("Неизвестная ошибка"));

            mockMvc.perform(get("/tasks/{id}", 14L))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("Ошибка сервера: Неизвестная ошибка"));
            ;
        }
    }
}
