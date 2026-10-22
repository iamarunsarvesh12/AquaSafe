package com.aquasafe.controller;

import com.aquasafe.dto.LoginRequestDTO;
import com.aquasafe.dto.LoginResponseDTO;
import com.aquasafe.entity.User;
import com.aquasafe.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String mockToken = UUID.randomUUID().toString();

        return ResponseEntity.ok(new LoginResponseDTO(
            true,
            user.getUsername(),
            user.getRole(),
            mockToken,
            "Authentication successful"
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.ok(new LoginResponseDTO(false, "guest", "GUEST", null, "Not authenticated"));
        }
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        String role = user != null ? user.getRole() : "USER";
        return ResponseEntity.ok(new LoginResponseDTO(true, username, role, null, "Authenticated"));
    }
}
