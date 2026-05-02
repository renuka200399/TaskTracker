package com.renuka.tasktracker.controller;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.renuka.tasktracker.config.SecurityUtil;
import com.renuka.tasktracker.entity.*;
import com.renuka.tasktracker.service.TaskService;
import com.renuka.tasktracker.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") //  FIX FOR DEPLOYMENT
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final SecurityUtil securityUtil;

    //  CREATE TASK
    @PostMapping
    public Task createTask(@RequestBody Map<String, Object> body,
                           HttpServletRequest request) {

        User current = securityUtil.getCurrentUser(request);

        if (current == null) {
            throw new RuntimeException("Unauthorized");
        }

        if (current.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admin can assign tasks");
        }

        //  Build Task object
        Task task = new Task();

        task.setTitle((String) body.get("title"));
        task.setDescription((String) body.get("description"));

        //  REQUIRED FIELDS (you had validation in service)
        task.setPriority(Priority.valueOf(body.get("priority").toString()));
        task.setDueDate(LocalDate.parse(body.get("dueDate").toString()));

        Long userId = Long.valueOf(body.get("userId").toString());
        Long projectId = Long.valueOf(body.get("projectId").toString());

        return taskService.createTask(task, projectId, userId);
    }

    //  GET USER TASKS
    @GetMapping("/user/{userId}")
    public List<Task> getUserTasks(@PathVariable Long userId,
                                  HttpServletRequest request) {

        User current = securityUtil.getCurrentUser(request);

        if (current == null) {
            throw new RuntimeException("Unauthorized");
        }

        if (!current.getId().equals(userId) && current.getRole() != Role.ADMIN) {
            throw new RuntimeException("Unauthorized");
        }

        User user = userService.getById(userId);
        return taskService.getUserTasks(user);
    }

    // UPDATE TASK STATUS
    @PutMapping("/{taskId}")
    public Task updateStatus(@PathVariable Long taskId,
                            @RequestParam String status,
                            HttpServletRequest request) {

        User current = securityUtil.getCurrentUser(request);

        if (current == null) {
            throw new RuntimeException("Unauthorized");
        }

        Task task = taskService.getById(taskId);

        if (!task.getAssignedTo().getId().equals(current.getId())) {
            throw new RuntimeException("You can only update your own task");
        }

        // SAFE ENUM CONVERSION
        Status newStatus = Status.valueOf(status);

        return taskService.updateStatus(taskId, newStatus);
    }

    // OVERDUE TASKS
    @GetMapping("/overdue/{userId}")
    public List<Task> getUserOverdueTasks(@PathVariable Long userId,
                                         HttpServletRequest request) {

        User current = securityUtil.getCurrentUser(request);

        if (current == null) {
            throw new RuntimeException("Unauthorized");
        }

        if (!current.getId().equals(userId) && current.getRole() != Role.ADMIN) {
            throw new RuntimeException("Unauthorized");
        }

        User user = userService.getById(userId);
        return taskService.getOverdueTasksByUser(user);
    }

    //  PROJECT TASKS
    @GetMapping("/project/{projectId}")
    public List<Task> getProjectTasks(@PathVariable Long projectId,
                                     HttpServletRequest request) {

        User current = securityUtil.getCurrentUser(request);

        if (current == null) {
            throw new RuntimeException("Unauthorized");
        }

        return taskService.getTasksByProject(projectId);
    }

    //  TASK COUNT PER USER
    @GetMapping("/tasks-per-user")
    public Map<String, Long> getTasksPerUser(HttpServletRequest request) {

        User current = securityUtil.getCurrentUser(request);

        if (current == null || current.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admin allowed");
        }

        return taskService.getTaskCountPerUser();
    }

    //  ALL TASKS
    @GetMapping("/all")
    public List<Task> getAllTasks(HttpServletRequest request) {

        User current = securityUtil.getCurrentUser(request);

        if (current == null || current.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admin allowed");
        }

        return taskService.getAllTasks();
    }
}