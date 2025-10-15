package com.finova.planning.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Authentication response DTO for SSO operations
 */
public class AuthenticationResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    private String tokenType = "Bearer";

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("scope")
    private String scope;

    private UserDTO user;

    private LocalDateTime timestamp;

    private String message;

    private boolean authenticated;

    // Default constructor
    public AuthenticationResponse() {
        this.timestamp = LocalDateTime.now();
        this.authenticated = false;
    }

    // Constructor for successful authentication
    public AuthenticationResponse(String accessToken, String refreshToken, Long expiresIn, UserDTO user) {
        this();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.user = user;
        this.authenticated = true;
        this.message = "Authentication successful";
    }

    // Constructor for authentication failure
    public AuthenticationResponse(String message) {
        this();
        this.message = message;
        this.authenticated = false;
    }

    // Static factory methods
    public static AuthenticationResponse success(String accessToken, String refreshToken, Long expiresIn, UserDTO user) {
        return new AuthenticationResponse(accessToken, refreshToken, expiresIn, user);
    }

    public static AuthenticationResponse failure(String message) {
        return new AuthenticationResponse(message);
    }

    public static AuthenticationResponse userInfo(UserDTO user) {
        AuthenticationResponse response = new AuthenticationResponse();
        response.user = user;
        response.authenticated = true;
        response.message = "User information retrieved successfully";
        return response;
    }

    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    @Override
    public String toString() {
        return "AuthenticationResponse{" +
                "tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", user=" + user +
                ", timestamp=" + timestamp +
                ", message='" + message + '\'' +
                ", authenticated=" + authenticated +
                '}';
    }
}
