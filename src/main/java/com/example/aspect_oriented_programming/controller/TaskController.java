package com.example.aspect_oriented_programming.controller;

import com.example.aspect_oriented_programming.entity.Task;
import com.example.aspect_oriented_programming.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        logger.info("Создание задачи: {}", task);
        return ResponseEntity.ok(taskService.createTask(task));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        logger.info("Получение задачи с ID {}", id);
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("Задача с ID {} не найдена", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping
    public List<Task> getAllTasks() {
        logger.info("Получение всех задач");
        return taskService.getAllTasks();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @Valid @RequestBody Task task) {
        logger.info("Обновление задачи с ID {}: {}", id, task);
        try {
            return ResponseEntity.ok(taskService.updateTask(id, task));
        } catch (RuntimeException ex) {
            logger.error("Ошибка при обновлении задачи с ID {}: {}", id, ex.getMessage());
            throw ex;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        logger.info("Удаление задачи с ID {}", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
