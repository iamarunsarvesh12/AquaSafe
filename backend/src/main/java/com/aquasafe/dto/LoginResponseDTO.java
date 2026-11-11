package com.aquasafe.dto;

public class LoginResponseDTO {

    private boolean authenticated;
    private String username;
    private String role;
    private String token;
    private String message;
    private String assignedDistrict;
    private Boolean active;

    public LoginResponseDTO() {}

    public LoginResponseDTO(boolean authenticated, String username, String role, String token, String message) {
        this.authenticated = authenticated;
        this.username = username;
        this.role = role;
        this.token = token;
        this.message = message;
        this.assignedDistrict = "All Districts";
        this.active = true;
    }

    public LoginResponseDTO(boolean authenticated, String username, String role, String token, String message, String assignedDistrict, Boolean active) {
        this.authenticated = authenticated;
        this.username = username;
        this.role = role;
        this.token = token;
        this.message = message;
        this.assignedDistrict = assignedDistrict;
        this.active = active;
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

    public String getAssignedDistrict() { return assignedDistrict; }
    public void setAssignedDistrict(String assignedDistrict) { this.assignedDistrict = assignedDistrict; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
