package com.example.aspect_oriented_programming.repository;

import com.example.aspect_oriented_programming.BaseIntegrationTest;
import com.example.aspect_oriented_programming.entity.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class TaskRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TaskRepository taskRepository;


    @Test
    void givenTask_whenSave_thenFindById() {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Description");
        task.setUserId(1L);

        Task savedTask = taskRepository.save(task);

        assertThat(savedTask).isNotNull();
        assertThat(savedTask.getId()).isNotNull();

        Task foundTask = taskRepository.findById(savedTask.getId()).orElse(null);
        assertThat(foundTask).isNotNull();
        assertThat(foundTask.getTitle()).isEqualTo("Test Task");
    }

    @Test
    void givenMultipleTasks_whenFindAll_thenReturnAllTasks() {
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setUserId(1L);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setUserId(2L);
        taskRepository.save(task2);

        List<Task> tasks = taskRepository.findAll();

        assertThat(tasks).hasSize(2);
    }

    @Test
    void givenTask_whenUpdate_thenSaveUpdatedTask() {
        Task task = new Task();
        task.setTitle("Initial Task");
        task.setDescription("Initial Description");
        task.setUserId(1L);

        Task savedTask = taskRepository.save(task);

        savedTask.setTitle("Updated Task");
        Task updatedTask = taskRepository.save(savedTask);

        assertThat(updatedTask.getTitle()).isEqualTo("Updated Task");
    }

    @Test
    void givenTaskId_whenDelete_thenTaskNotFound() {
        Task task = new Task();
        task.setTitle("Task to Delete");
        task.setUserId(1L);

        Task savedTask = taskRepository.save(task);
        taskRepository.deleteById(savedTask.getId());

        Task deletedTask = taskRepository.findById(savedTask.getId()).orElse(null);
        assertThat(deletedTask).isNull();
    }
}
