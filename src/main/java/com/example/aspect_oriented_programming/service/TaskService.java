package com.example.aspect_oriented_programming.service;

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
        kafkaProducerService.sendMessage(TOPIC_NAME, "Создана задача: " +  createdTask);
        return createdTask;
    }

    public Task getTaskById(Long id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID не может быть отрицательным.");
        }
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Задача с ID " + id + " не найдена"));
        kafkaProducerService.sendMessage(TOPIC_NAME, "Получена задача: " + task);
        return task;
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        kafkaProducerService.sendMessage(TOPIC_NAME, "Получен список всех задач, количество: " + tasks.size());
        return tasks;
    }

    public Task updateTask(Long id, Task updatedTask) {
        return taskRepository.findById(id).map(task -> {
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setUserId(updatedTask.getUserId());
            Task savedTask = taskRepository.save(task);
            kafkaProducerService.sendMessage(TOPIC_NAME, "Обновлена задача: " + savedTask);
            return savedTask;
        }).orElseThrow(() -> {
            String errorMessage = "Task not found with id: " + id;
            kafkaProducerService.sendMessage(TOPIC_NAME, errorMessage);
            return new RuntimeException(errorMessage);
        });
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            String errorMessage = "Task not found with id: " + id;
            kafkaProducerService.sendMessage(TOPIC_NAME, errorMessage);
            throw new RuntimeException(errorMessage);
        }
        taskRepository.deleteById(id);
        kafkaProducerService.sendMessage(TOPIC_NAME, "Удалена задача с id: " + id);
    }
}

