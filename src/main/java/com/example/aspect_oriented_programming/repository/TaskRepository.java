package com.example.aspect_oriented_programming.repository;

import com.example.aspect_oriented_programming.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
