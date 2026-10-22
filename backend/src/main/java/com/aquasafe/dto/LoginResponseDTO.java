package com.aquasafe.dto;

public class LoginResponseDTO {

    private boolean authenticated;
    private String username;
    private String role;
    private String token;
    private String message;

    public LoginResponseDTO() {}

    public LoginResponseDTO(boolean authenticated, String username, String role, String token, String message) {
        this.authenticated = authenticated;
        this.username = username;
        this.role = role;
        this.token = token;
        this.message = message;
    }

    public boolean isAuthenticated() { return authenticated; }
    public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
