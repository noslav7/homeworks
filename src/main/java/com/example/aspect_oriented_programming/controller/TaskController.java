package com.example.aspect_oriented_programming.controller;

import com.example.aspect_oriented_programming.dto.TaskDTO;
import com.example.aspect_oriented_programming.entity.Task;
import com.example.aspect_oriented_programming.service.TaskService;
import com.example.aspect_oriented_programming.mapper.TaskMapper;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public TaskDTO createTask(@Valid @RequestBody TaskDTO taskDTO) {
        Task task = TaskMapper.toEntity(taskDTO);
        Task createdTask = taskService.createTask(task);
        return TaskMapper.toDTO(createdTask);
    }

    @GetMapping("/{id}")
    public TaskDTO getTaskById(@PathVariable Long id) {
        return TaskMapper.toDTO(taskService.getTaskById(id));
    }

    @GetMapping
    public List<TaskDTO> getAllTasks() {
        return taskService.getAllTasks().stream()
                .map(TaskMapper::toDTO)
                .toList();
    }

    @PutMapping("/{id}")
    public TaskDTO updateTask(@PathVariable Long id, @Valid @RequestBody TaskDTO taskDTO) {
        Task task = TaskMapper.toEntity(taskDTO);
        Task updatedTask = taskService.updateTask(id, task);
        return TaskMapper.toDTO(updatedTask);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}

