package com.renuka.tasktracker.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.renuka.tasktracker.entity.Project;
import com.renuka.tasktracker.entity.User;
import com.renuka.tasktracker.entity.Status;
import com.renuka.tasktracker.repository.ProjectRepository;
import com.renuka.tasktracker.repository.UserRepository;
import com.renuka.tasktracker.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository; //  NEW

    // CREATE PROJECT
    public Project createProject(Project project, Long creatorId) {

        if (project.getName() == null || project.getName().isBlank()) {
            throw new RuntimeException("Project name is required");
        }

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        project.setCreatedBy(creator);

        Set<User> members = new HashSet<>();
        members.add(creator);

        // if members passed from frontend → include them also
        if (project.getMembers() != null) {
            members.addAll(project.getMembers());
        }

        project.setMembers(members);

        return projectRepository.save(project);
    }

    //  GET ALL PROJECTS
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    // GET USER PROJECTS
    public List<Project> getUserProjects(User user) {
        return projectRepository.findByMembersContaining(user);
    }

    //  ADD MEMBER
    public Project addMember(Long projectId, Long userId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (project.getMembers().contains(user)) {
            throw new RuntimeException("User already in project");
        }

        project.getMembers().add(user);

        return projectRepository.save(project);
    }

    // UPDATE PROJECT
    public Project updateProject(Long projectId, Project updatedProject) {

        Project existing = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (updatedProject.getName() != null && !updatedProject.getName().isBlank()) {
            existing.setName(updatedProject.getName());
        }

        if (updatedProject.getDescription() != null) {
            existing.setDescription(updatedProject.getDescription());
        }

        return projectRepository.save(existing);
    }

    //  DELETE PROJECT
    public void deleteProject(Long projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new RuntimeException("Project not found");
        }

        projectRepository.deleteById(projectId);
    }

    // NEW FEATURE → PROJECT PROGRESS
    public int calculateProgress(Project project) {

        long total = taskRepository.countByProject(project);
        long done = taskRepository.countByProjectAndStatus(project, Status.DONE);

        if (total == 0) return 0;

        return (int) ((done * 100) / total);
    }
}