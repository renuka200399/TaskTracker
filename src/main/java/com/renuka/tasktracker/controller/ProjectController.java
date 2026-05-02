package com.renuka.tasktracker.controller;

import java.util.*;

import org.springframework.web.bind.annotation.*;

import com.renuka.tasktracker.entity.Project;
import com.renuka.tasktracker.entity.User;
import com.renuka.tasktracker.service.ProjectService;
import com.renuka.tasktracker.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@CrossOrigin
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    // ✅ CREATE PROJECT
    @PostMapping
    public Project createProject(@RequestBody Map<String, Object> body) {

        String name = (String) body.get("name");
        String description = (String) body.get("description");
        Long createdBy = Long.valueOf(body.get("createdBy").toString());

        List<Integer> memberIdsRaw = (List<Integer>) body.get("members");

        Set<User> members = new HashSet<>();

        if (memberIdsRaw != null) {
            for (Integer id : memberIdsRaw) {
                members.add(userService.getById(Long.valueOf(id)));
            }
        }

        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        project.setMembers(members);

        return projectService.createProject(project, createdBy);
    }

    //  GET ALL PROJECTS
    @GetMapping
    public List<Map<String, Object>> getAllProjects() {

        List<Project> projects = projectService.getAllProjects();

        return projects.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("description", p.getDescription());
            map.put("members", p.getMembers());
            map.put("progress", projectService.calculateProgress(p)); //  IMPORTANT
            return map;
        }).toList();
    }

    //  GET PROJECTS BY USER
    @GetMapping("/{userId}")
    public List<Map<String, Object>> getUserProjects(@PathVariable Long userId) {

        User user = userService.getById(userId);

        List<Project> projects = projectService.getUserProjects(user);

        return projects.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("description", p.getDescription());
            map.put("members", p.getMembers());
            map.put("progress", projectService.calculateProgress(p)); // IMPORTANT
            return map;
        }).toList();
    }

    // ADD MEMBER
    @PutMapping("/{projectId}/add-member/{userId}")
    public Project addMember(@PathVariable Long projectId,
                             @PathVariable Long userId) {

        return projectService.addMember(projectId, userId);
    }

    //  UPDATE PROJECT
    @PutMapping("/{projectId}")
    public Project updateProject(@PathVariable Long projectId,
                                @RequestBody Project project) {

        return projectService.updateProject(projectId, project);
    }

    //  DELETE PROJECT
    @DeleteMapping("/{projectId}")
    public void deleteProject(@PathVariable Long projectId) {

        projectService.deleteProject(projectId);
    }
}