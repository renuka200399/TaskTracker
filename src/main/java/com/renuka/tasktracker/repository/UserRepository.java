package com.renuka.tasktracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renuka.tasktracker.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}