package com.renuka.tasktracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renuka.tasktracker.entity.Project;
import com.renuka.tasktracker.entity.User;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByMembersContaining(User user);
}