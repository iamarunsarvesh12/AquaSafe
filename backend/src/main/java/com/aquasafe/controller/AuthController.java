package com.aquasafe.controller;

import com.aquasafe.dto.LoginRequestDTO;
import com.aquasafe.dto.LoginResponseDTO;
import com.aquasafe.entity.User;
import com.aquasafe.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getActive() != null && !user.getActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new LoginResponseDTO(false, request.getUsername(), null, null, "Account has been deactivated. Contact System Administrator."));
        }

        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        String token = UUID.randomUUID().toString();

        return ResponseEntity.ok(new LoginResponseDTO(
            true,
            user.getUsername(),
            user.getRole(),
            token,
            "Authentication successful",
            user.getAssignedDistrict() != null ? user.getAssignedDistrict() : "All Districts",
            user.getActive() != null ? user.getActive() : true
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.ok(new LoginResponseDTO(false, "guest", "GUEST", null, "Not authenticated", "All Districts", true));
        }
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.ok(new LoginResponseDTO(false, username, "USER", null, "User profile not found", "All Districts", true));
        }
        return ResponseEntity.ok(new LoginResponseDTO(
            true,
            username,
            user.getRole(),
            null,
            "Authenticated",
            user.getAssignedDistrict() != null ? user.getAssignedDistrict() : "All Districts",
            user.getActive() != null ? user.getActive() : true
        ));
    }
}
