package com.renuka.tasktracker.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    // password will NOT be sent in response (important )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Projects where user is a MEMBER
    @ManyToMany(mappedBy = "members")
    @JsonIgnore
    private Set<Project> projects;

    // Projects created by this user (ADMIN)
    @OneToMany(mappedBy = "createdBy")
    @JsonIgnore
    private Set<Project> createdProjects;
}