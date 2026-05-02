package com.renuka.tasktracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renuka.tasktracker.entity.*;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssignedTo(User user);

    List<Task> findByProjectId(Long projectId);

    //  ADD THESE
    long countByProject(Project project);

    long countByProjectAndStatus(Project project, Status status);
}