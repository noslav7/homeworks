package com.example.aspect_oriented_programming.controller;

import com.example.aspect_oriented_programming.BaseIntegrationTest;
import com.example.aspect_oriented_programming.dto.TaskDTO;
import com.example.aspect_oriented_programming.entity.Task;
import com.example.aspect_oriented_programming.mapper.TaskMapper;
import com.example.aspect_oriented_programming.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class TaskControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    Long savedTaskId;

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Test Task");
        taskDTO.setDescription("Integration Test Task");
        taskDTO.setUserId(1L);

        Task task = TaskMapper.toEntity(taskDTO);
        task = taskRepository.save(task);
        savedTaskId = task.getId();
    }


    @Test
    void givenTaskDto_whenCreateTask_thenReturnTaskDto() throws Exception {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Test Task");
        taskDTO.setDescription("Integration Test Task");
        taskDTO.setUserId(1L);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void givenValidId_whenGetTaskById_thenReturnTaskDto() throws Exception {
        mockMvc.perform(get("/tasks/{id}", savedTaskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedTaskId));
    }

    @Test
    void givenTasks_whenGetAllTasks_thenReturnList() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void givenTaskDto_whenUpdateTask_thenReturnUpdatedTask() throws Exception {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Updated Task");
        taskDTO.setDescription("Updated Description");
        taskDTO.setUserId(1L);

        mockMvc.perform(put("/tasks/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"));
    }
}
