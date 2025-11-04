package com.finova.user.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for LoginResponse DTO
 */
@DisplayName("LoginResponse DTO Tests")
class LoginResponseTest {

    @Test
    @DisplayName("Should create LoginResponse with default constructor")
    void shouldCreateLoginResponseWithDefaultConstructor() {
        LoginResponse response = new LoginResponse();
        
        assertThat(response).isNotNull();
        assertThat(response.getType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("Should create successful LoginResponse with token")
    void shouldCreateSuccessfulLoginResponseWithToken() {
        LoginResponse response = new LoginResponse(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            1L,
            "johndoe",
            "john.doe@example.com"
        );
        
        assertThat(response.getToken()).isEqualTo("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("johndoe");
        assertThat(response.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(response.getMessage()).isEqualTo("Login successful");
        assertThat(response.getType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("Should create error LoginResponse with message")
    void shouldCreateErrorLoginResponseWithMessage() {
        LoginResponse response = new LoginResponse("Invalid username or password");
        
        assertThat(response.getMessage()).isEqualTo("Invalid username or password");
        assertThat(response.getToken()).isNull();
        assertThat(response.getId()).isNull();
        assertThat(response.getUsername()).isNull();
        assertThat(response.getEmail()).isNull();
    }

    @Test
    @DisplayName("Should set and get token")
    void shouldSetAndGetToken() {
        LoginResponse response = new LoginResponse();
        response.setToken("newToken123");
        
        assertThat(response.getToken()).isEqualTo("newToken123");
    }

    @Test
    @DisplayName("Should set and get type")
    void shouldSetAndGetType() {
        LoginResponse response = new LoginResponse();
        response.setType("JWT");
        
        assertThat(response.getType()).isEqualTo("JWT");
    }

    @Test
    @DisplayName("Should set and get user ID")
    void shouldSetAndGetUserId() {
        LoginResponse response = new LoginResponse();
        response.setId(42L);
        
        assertThat(response.getId()).isEqualTo(42L);
    }

    @Test
    @DisplayName("Should set and get username")
    void shouldSetAndGetUsername() {
        LoginResponse response = new LoginResponse();
        response.setUsername("testuser");
        
        assertThat(response.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should set and get email")
    void shouldSetAndGetEmail() {
        LoginResponse response = new LoginResponse();
        response.setEmail("test@example.com");
        
        assertThat(response.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should set and get message")
    void shouldSetAndGetMessage() {
        LoginResponse response = new LoginResponse();
        response.setMessage("Custom message");
        
        assertThat(response.getMessage()).isEqualTo("Custom message");
    }

    @Test
    @DisplayName("Should handle authentication failure response")
    void shouldHandleAuthenticationFailureResponse() {
        LoginResponse response = new LoginResponse("Account is disabled");
        
        assertThat(response.getMessage()).isEqualTo("Account is disabled");
        assertThat(response.getToken()).isNull();
    }

    @Test
    @DisplayName("Should handle complete successful login response")
    void shouldHandleCompleteSuccessfulLoginResponse() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        LoginResponse response = new LoginResponse(token, 100L, "janesmith", "jane.smith@finova.com");
        
        assertThat(response.getToken()).isEqualTo(token);
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getUsername()).isEqualTo("janesmith");
        assertThat(response.getEmail()).isEqualTo("jane.smith@finova.com");
        assertThat(response.getMessage()).isEqualTo("Login successful");
    }
}
