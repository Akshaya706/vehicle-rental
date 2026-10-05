package com.rental.dto;

public class LoginResponse {
    private String message;
    private boolean success;
    private Long userId;
    private String username;
    private String role;
    private String name;
    private Long customerId;

    public LoginResponse() {}

    public LoginResponse(String message, boolean success, Long userId, String username, String role, String name, Long customerId) {
        this.message = message;
        this.success = success;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.name = name;
        this.customerId = customerId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
