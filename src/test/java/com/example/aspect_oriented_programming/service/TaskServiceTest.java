package com.example.aspect_oriented_programming.service;

import com.example.aspect_oriented_programming.dto.KafkaMessageDTO;
import com.example.aspect_oriented_programming.entity.Task;
import com.example.aspect_oriented_programming.kafka.KafkaProducerService;
import com.example.aspect_oriented_programming.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private TaskService taskService;

    private Task validTask;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        validTask = new Task();
        validTask.setId(1L);
        validTask.setTitle("Test Title");
        validTask.setDescription("Test Description");
        validTask.setUserId(100L);
    }

    @Nested
    @DisplayName("createTask() testing")
    class CreateTaskTests {
        @Test
        void givenValidTask_whenCreateTask_thenSavesAndSendsMessage() {
            when(taskRepository.save(any(Task.class))).thenReturn(validTask);

            Task created = taskService.createTask(validTask);

            assertNotNull(created);
            assertEquals(validTask.getId(), created.getId());
            verify(taskRepository, times(1)).save(any(Task.class));
            verify(kafkaProducerService, times(1))
                    .sendMessage(eq("test-topic"), any(KafkaMessageDTO.class));
        }
    }

    @Nested
    @DisplayName("getTaskById() testing")
    class GetTaskByIdTests {

        @Test
        void givenNegativeId_whenGetTaskById_thenThrowExceptionAndNoKafka() {
            assertThrows(IllegalArgumentException.class, () -> taskService.getTaskById(-1L));

            verify(kafkaProducerService, never())
                    .sendMessage(eq("test-topic"), any(KafkaMessageDTO.class));
            verifyNoInteractions(taskRepository);
        }

        @Test
        void givenNotFoundId_whenGetTaskById_thenThrowNoSuchElementExceptionAndNoMessageSent() {
            when(taskRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> taskService.getTaskById(999L));

            verify(kafkaProducerService, never())
                    .sendMessage(eq("test-topic"), any(KafkaMessageDTO.class));
        }

        @Test
        void givenValidId_whenGetTaskById_thenReturnTaskAndSendMessage() {
            when(taskRepository.findById(1L)).thenReturn(Optional.of(validTask));

            Task found = taskService.getTaskById(1L);

            assertNotNull(found);
            assertEquals("Test Title", found.getTitle());
            verify(taskRepository, times(1)).findById(1L);
            verify(kafkaProducerService, times(1))
                    .sendMessage(eq("test-topic"), any(KafkaMessageDTO.class));
        }
    }

    @Nested
    @DisplayName("getAllTasks() testing")
    class GetAllTasksTests {
        @Test
        void givenTasks_whenGetAllTasks_thenReturnsListAndSendsMessage() {
            when(taskRepository.findAll()).thenReturn(List.of(validTask));

            List<Task> tasks = taskService.getAllTasks();
            assertEquals(1, tasks.size());

            verify(taskRepository, times(1)).findAll();
            verify(kafkaProducerService, times(1))
                    .sendMessage(eq("test-topic"), any(KafkaMessageDTO.class));
        }

        @Test
        void givenNoTasks_whenGetAllTasks_thenEmptyListAndSendsMessage() {
            when(taskRepository.findAll()).thenReturn(Collections.emptyList());

            List<Task> tasks = taskService.getAllTasks();
            assertTrue(tasks.isEmpty());

            verify(taskRepository, times(1)).findAll();
            verify(kafkaProducerService, times(1))
                    .sendMessage(eq("test-topic"), any(KafkaMessageDTO.class));
        }
    }

    @Nested
    @DisplayName("updateTask() testing")
    class UpdateTaskTests {
        @Test
        void givenExistingId_whenUpdateTask_thenUpdateAndSendMessage() {
            Task updateInfo = new Task();
            updateInfo.setTitle("Updated Title");

            when(taskRepository.findById(1L)).thenReturn(Optional.of(validTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

            Task updated = taskService.updateTask(1L, updateInfo);
            assertEquals("Updated Title", updated.getTitle());

            verify(kafkaProducerService, times(1))
                    .sendMessage(eq("test-topic"), any(KafkaMessageDTO.class));
        }

        @Test
        void givenNonExistingId_whenUpdateTask_thenThrowAndSendMessage() {
            when(taskRepository.findById(999L)).thenReturn(Optional.empty());

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> taskService.updateTask(999L, validTask));
            assertTrue(ex.getMessage().contains("999"));

            verify(kafkaProducerService, times(1))
                    .sendMessage(eq("test-topic"), any(KafkaMessageDTO.class));
        }
    }

    @Nested
    @DisplayName("deleteTask() testing")
    class DeleteTaskTests {

        @Test
        void givenExistingId_whenDeleteTask_thenSuccessAndSendMessage() {
            when(taskRepository.existsById(1L)).thenReturn(true);

            taskService.deleteTask(1L);

            verify(taskRepository, times(1)).deleteById(1L);
            verify(kafkaProducerService, times(1))
                    .sendMessage(eq("test-topic"), any(KafkaMessageDTO.class));
        }

        @Test
        void givenNonExistingId_whenDeleteTask_thenThrowAndSendMessage() {
            when(taskRepository.existsById(999L)).thenReturn(false);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> taskService.deleteTask(999L));
            assertTrue(ex.getMessage().contains("999"));

            verify(kafkaProducerService, times(1))
                    .sendMessage(eq("test-topic"), any(KafkaMessageDTO.class));
            verify(taskRepository, never()).deleteById(999L);
        }
    }
}
