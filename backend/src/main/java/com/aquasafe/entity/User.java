package com.aquasafe.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_username", columnList = "username"),
    @Index(name = "idx_users_role", columnList = "role")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 30)
    private String role = "USER"; // ADMIN, USER (Public Health Analyst), DHO (District Health Officer)

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(length = 100)
    private String email;

    @Column(length = 100)
    private String department;

    @Column(name = "assigned_district", length = 100)
    private String assignedDistrict = "All Districts";

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public User() {}

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.assignedDistrict = "All Districts";
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public User(String username, String password, String role, String fullName, String email, String department) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
        this.assignedDistrict = "All Districts";
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public User(String username, String password, String role, String fullName, String email, String department, String assignedDistrict) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
        this.assignedDistrict = assignedDistrict != null ? assignedDistrict : "All Districts";
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getAssignedDistrict() { return assignedDistrict; }
    public void setAssignedDistrict(String assignedDistrict) { this.assignedDistrict = assignedDistrict; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
