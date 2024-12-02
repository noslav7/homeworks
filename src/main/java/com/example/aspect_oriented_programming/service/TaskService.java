package com.example.aspect_oriented_programming.service;

import com.example.aspect_oriented_programming.entity.Task;
import com.example.aspect_oriented_programming.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
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

