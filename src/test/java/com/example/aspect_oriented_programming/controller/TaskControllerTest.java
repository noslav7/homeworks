package com.example.aspect_oriented_programming.controller;

import com.example.aspect_oriented_programming.dto.TaskDTO;
import com.example.aspect_oriented_programming.mapper.TaskMapper;
import com.example.aspect_oriented_programming.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskDTO validTaskDTO;

    @BeforeEach
    void setUp() {
        validTaskDTO = new TaskDTO();
        validTaskDTO.setId(1L);
        validTaskDTO.setTitle("Test Title");
        validTaskDTO.setDescription("Test Description");
        validTaskDTO.setUserId(100L);
    }

    @Nested
    @DisplayName("createTask() testing")
    class CreateTaskTests {

        @Test
        void givenValidTaskDTO_whenCreateTask_thenReturnCreatedTaskDTO() throws Exception {
            Mockito.when(taskService.createTask(any()))
                    .thenAnswer(invocation -> invocation.getArguments()[0]);

            mockMvc.perform(post("/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validTaskDTO)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(validTaskDTO.getId()))
                    .andExpect(jsonPath("$.title").value(validTaskDTO.getTitle()))
                    .andExpect(jsonPath("$.description").value(validTaskDTO.getDescription()))
                    .andExpect(jsonPath("$.userId").value(validTaskDTO.getUserId()));
        }

        @Test
        void givenInvalidTaskDTO_whenCreateTask_thenReturnBadRequest() throws Exception {
            TaskDTO invalidDto = new TaskDTO();
            invalidDto.setTitle("");
            invalidDto.setUserId(99L);

            mockMvc.perform(post("/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").exists());
        }
    }

    @Nested
    @DisplayName("getTaskById() testing")
    class GetTaskByIdTests {

        @Test
        void givenExistingId_whenGetTaskById_thenReturnTaskDTO() throws Exception {
            Mockito.when(taskService.getTaskById(1L))
                    .thenReturn(TaskMapper.toEntity(validTaskDTO));

            mockMvc.perform(get("/tasks/{id}", 1L))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(validTaskDTO.getId()))
                    .andExpect(jsonPath("$.title").value(validTaskDTO.getTitle()))
                    .andExpect(jsonPath("$.userId").value(validTaskDTO.getUserId()));
        }

        @Test
        void givenNegativeId_whenGetTaskById_thenThrowBadRequest() throws Exception {
            Mockito.when(taskService.getTaskById(-1L))
                    .thenThrow(new IllegalArgumentException("ID не может быть отрицательным."));

            mockMvc.perform(get("/tasks/{id}", -1L))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("ID не может быть отрицательным."));
        }

        @Test
        void givenNonExistingId_whenGetTaskById_thenReturnNotFound() throws Exception {
            Mockito.when(taskService.getTaskById(999L))
                    .thenThrow(new java.util.NoSuchElementException("Задача с ID 999 не найдена"));

            mockMvc.perform(get("/tasks/{id}", 999L))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Задача с ID 999 не найдена"));
        }
    }

    @Nested
    @DisplayName("getAllTasks() testing")
    class GetAllTasksTests {

        @Test
        void givenNoTasks_whenGetAllTasks_thenReturnEmptyList() throws Exception {
            Mockito.when(taskService.getAllTasks()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/tasks"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        void givenTasksExist_whenGetAllTasks_thenReturnList() throws Exception {
            Mockito.when(taskService.getAllTasks())
                    .thenReturn(List.of(TaskMapper.toEntity(validTaskDTO)));

            mockMvc.perform(get("/tasks"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].title").value("Test Title"));
        }
    }

    @Nested
    @DisplayName("updateTask() testing")
    class UpdateTaskTests {

        @Test
        void givenValidIdAndDTO_whenUpdateTask_thenReturnUpdatedTask() throws Exception {
            Mockito.when(taskService.updateTask(eq(1L), any()))
                    .thenAnswer(invocation -> invocation.getArguments()[1]);

            validTaskDTO.setTitle("Updated Title");

            mockMvc.perform(put("/tasks/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validTaskDTO)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Updated Title"));
        }

        @Test
        void givenNonExistingId_whenUpdateTask_thenReturnException() throws Exception {
            Mockito.when(taskService.updateTask(eq(999L), any()))
                    .thenThrow(new RuntimeException("Задача с ID 999 не найдена"));

            mockMvc.perform(put("/tasks/{id}", 999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validTaskDTO)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("Ошибка сервера: Задача с ID 999 не найдена"));
        }
    }

    @Nested
    @DisplayName("deleteTask() testing")
    class DeleteTaskTests {

        @Test
        void givenValidId_whenDeleteTask_thenReturnOk() throws Exception {
            Mockito.doNothing().when(taskService).deleteTask(1L);

            mockMvc.perform(delete("/tasks/{id}", 1L))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        void givenNonExistingId_whenDeleteTask_thenReturnException() throws Exception {
            Mockito.doThrow(new RuntimeException("Задача с ID 999 не найдена"))
                    .when(taskService).deleteTask(999L);

            mockMvc.perform(delete("/tasks/{id}", 999L))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("Ошибка сервера: Задача с ID 999 не найдена"));
        }
    }
}
