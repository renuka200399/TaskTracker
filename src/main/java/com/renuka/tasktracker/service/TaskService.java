package com.renuka.tasktracker.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.renuka.tasktracker.entity.*;
import com.renuka.tasktracker.repository.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    
    public Task createTask(Task task, Long projectId, Long userId) {

        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new RuntimeException("Task title is required");
        }

        if (task.getDueDate() == null) {
            throw new RuntimeException("Due date is required");
        }

        if (task.getPriority() == null) {
            throw new RuntimeException("Priority is required");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!project.getMembers().contains(user)) {
            throw new RuntimeException("User is not part of this project");
        }

        task.setProject(project);
        task.setAssignedTo(user);
        task.setStatus(Status.TODO);

        return taskRepository.save(task);
    }

    public List<Task> getUserTasks(User user) {
        return taskRepository.findByAssignedTo(user);
    }

    public Task updateStatus(Long taskId, Status status) {

        if (status == null) {
            throw new RuntimeException("Status is required");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setStatus(status);
        return taskRepository.save(task);
    }

    public List<Task> getOverdueTasksByUser(User user) {
        return taskRepository.findByAssignedTo(user).stream()
                .filter(t -> t.getDueDate() != null)
                .filter(t -> t.getDueDate().isBefore(LocalDate.now())
                        && t.getStatus() != Status.DONE)
                .toList();
    }

    public List<Task> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public Map<String, Long> getTaskCountPerUser() {

        return taskRepository.findAll().stream()
                .filter(t -> t.getAssignedTo() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getAssignedTo().getEmail(),
                        Collectors.counting()
                ));
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }
}