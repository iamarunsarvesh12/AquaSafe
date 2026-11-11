package com.aquasafe.controller;

import com.aquasafe.entity.User;
import com.aquasafe.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(originPatterns = "*")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<Map<String, Object>> sanitized = new ArrayList<>();
        for (User u : users) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", u.getId());
            m.put("username", u.getUsername());
            m.put("role", u.getRole());
            m.put("fullName", u.getFullName() != null ? u.getFullName() : u.getUsername());
            m.put("email", u.getEmail() != null ? u.getEmail() : u.getUsername() + "@aquasafe.gov.in");
            m.put("department", u.getDepartment() != null ? u.getDepartment() : "Public Health Operations");
            m.put("assignedDistrict", u.getAssignedDistrict() != null ? u.getAssignedDistrict() : "All Districts");
            m.put("active", u.getActive() != null ? u.getActive() : true);
            m.put("createdAt", u.getCreatedAt());
            sanitized.add(m);
        }
        return ResponseEntity.ok(sanitized);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");
        String role = payload.getOrDefault("role", "USER");
        String fullName = payload.get("fullName");
        String email = payload.get("email");
        String department = payload.get("department");
        String assignedDistrict = payload.getOrDefault("assignedDistrict", "All Districts");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username and password are required."));
        }

        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest().body(Map.of("message", "User with username '" + username + "' already exists."));
        }

        User newUser = new User(username, passwordEncoder.encode(password), role.toUpperCase(), fullName, email, department, assignedDistrict);
        userRepository.save(newUser);

        return ResponseEntity.ok(Map.of(
            "message", "User created successfully",
            "userId", newUser.getId(),
            "username", newUser.getUsername(),
            "role", newUser.getRole(),
            "assignedDistrict", newUser.getAssignedDistrict()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        if ("admin".equalsIgnoreCase(user.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Cannot delete the primary system administrator account."));
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "User '" + user.getUsername() + "' removed successfully."));
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        if ("admin".equalsIgnoreCase(user.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Cannot deactivate the primary administrator."));
        }

        boolean currentStatus = user.getActive() != null ? user.getActive() : true;
        user.setActive(!currentStatus);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
            "message", "User status updated",
            "username", user.getUsername(),
            "active", user.getActive()
        ));
    }
}
