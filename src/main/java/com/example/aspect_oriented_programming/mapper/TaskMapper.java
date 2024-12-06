package com.example.aspect_oriented_programming.mapper;

import com.example.aspect_oriented_programming.dto.TaskDTO;
import com.example.aspect_oriented_programming.entity.Task;

public class TaskMapper {

    public static TaskDTO toDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setUserId(task.getUserId());
        return dto;
    }

    public static Task toEntity(TaskDTO dto) {
        Task task = new Task();
        task.setId(dto.getId());
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setUserId(dto.getUserId());
        return task;
    }
}

