package com.finova.user.dto;

/**
 * DTO for login responses
 */
public class LoginResponse {
    
    private boolean success = false;
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String message;
    private String errorMessage;
    
    // Constructors
    public LoginResponse() {}
    
    public LoginResponse(String token, Long id, String username, String email) {
        this.success = true;
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.message = "Login successful";
    }
    
    public LoginResponse(String errorMessage) {
        this.success = false;
        this.errorMessage = errorMessage;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    // Convenience getters for backward compatibility
    public Long getUserId() { return id; }
    public void setUserId(Long userId) { this.id = userId; }
}
