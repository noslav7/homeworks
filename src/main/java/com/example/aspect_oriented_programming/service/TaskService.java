package com.example.aspect_oriented_programming.service;

import com.example.aspect_oriented_programming.dto.KafkaMessageDTO;
import com.example.aspect_oriented_programming.entity.Task;
import com.example.aspect_oriented_programming.kafka.KafkaProducerService;
import com.example.aspect_oriented_programming.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final KafkaProducerService kafkaProducerService;
    private static final String TOPIC_NAME = "test-topic";

    public TaskService(TaskRepository taskRepository, KafkaProducerService kafkaProducerService) {
        this.taskRepository = taskRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    public Task createTask(Task task) {
        Task createdTask = taskRepository.save(task);
        KafkaMessageDTO messageDTO = new KafkaMessageDTO();
        messageDTO.setTitle("Создана задача");
        messageDTO.setContent("Задача создана: " + createdTask);
        kafkaProducerService.sendMessage(TOPIC_NAME, messageDTO);
        return createdTask;
    }

    public Task getTaskById(Long id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID не может быть отрицательным.");
        }
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Задача с ID " + id + " не найдена"));
        KafkaMessageDTO messageDTO = new KafkaMessageDTO();
        messageDTO.setTitle("Получена задача");
        messageDTO.setContent("Задача получена: " + task);
        kafkaProducerService.sendMessage(TOPIC_NAME, messageDTO);
        return task;
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        KafkaMessageDTO messageDTO = new KafkaMessageDTO();
        messageDTO.setTitle("Получен список задач");
        messageDTO.setContent("Количество задач: " + tasks.size());
        kafkaProducerService.sendMessage(TOPIC_NAME, messageDTO);
        return tasks;
    }

    public Task updateTask(Long id, Task updatedTask) {
        return taskRepository.findById(id).map(task -> {
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setUserId(updatedTask.getUserId());
            Task savedTask = taskRepository.save(task);
            KafkaMessageDTO messageDTO = new KafkaMessageDTO();
            messageDTO.setTitle("Обновлена задача");
            messageDTO.setContent("Задача обновлена: " + savedTask);
            kafkaProducerService.sendMessage(TOPIC_NAME, messageDTO);
            return savedTask;
        }).orElseThrow(() -> {
            String errorMessage = "Task not found with id: " + id;
            KafkaMessageDTO messageDTO = new KafkaMessageDTO();
            messageDTO.setTitle("Ошибка обновления задачи");
            messageDTO.setContent(errorMessage);
            kafkaProducerService.sendMessage(TOPIC_NAME, messageDTO);
            return new RuntimeException(errorMessage);
        });
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            String errorMessage = "Task not found with id: " + id;
            KafkaMessageDTO messageDTO = new KafkaMessageDTO();
            messageDTO.setTitle("Ошибка удаления задачи");
            messageDTO.setContent(errorMessage);
            kafkaProducerService.sendMessage(TOPIC_NAME, messageDTO);
            throw new RuntimeException(errorMessage);
        }
        taskRepository.deleteById(id);
        KafkaMessageDTO messageDTO = new KafkaMessageDTO();
        messageDTO.setTitle("Удалена задача");
        messageDTO.setContent("Задача с id " + id + " удалена");
        kafkaProducerService.sendMessage(TOPIC_NAME, messageDTO);
    }
}
