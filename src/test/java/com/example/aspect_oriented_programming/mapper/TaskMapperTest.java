package com.example.aspect_oriented_programming.mapper;

import com.example.aspect_oriented_programming.dto.TaskDTO;
import com.example.aspect_oriented_programming.entity.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskMapperTest {

    @Test
    void givenTask_whenMapToDTO_thenReturnDTO() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Title");
        task.setDescription("Description");
        task.setUserId(123L);

        TaskDTO dto = TaskMapper.toDTO(task);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Title", dto.getTitle());
        assertEquals("Description", dto.getDescription());
        assertEquals(123L, dto.getUserId());
    }

    @Test
    void givenTaskDTO_whenMapToEntity_thenReturnEntity() {
        TaskDTO dto = new TaskDTO();
        dto.setId(2L);
        dto.setTitle("Title2");
        dto.setDescription("Description2");
        dto.setUserId(456L);

        Task task = TaskMapper.toEntity(dto);

        assertNotNull(task);
        assertEquals(2L, task.getId());
        assertEquals("Title2", task.getTitle());
        assertEquals("Description2", task.getDescription());
        assertEquals(456L, task.getUserId());
    }
}
