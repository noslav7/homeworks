package com.example.aspect_oriented_programming.service;

import com.example.aspect_oriented_programming.entity.Task;
import com.example.aspect_oriented_programming.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task getTaskById(Long id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID не может быть отрицательным.");
        }
        return taskRepository.findById(id).orElseThrow();
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task updateTask(Long id, Task updatedTask) {
        return taskRepository.findById(id).map(task -> {
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setUserId(updatedTask.getUserId());
            return taskRepository.save(task);
        }).orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}

