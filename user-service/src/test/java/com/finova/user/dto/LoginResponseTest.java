package com.finova.user.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LoginResponse DTO
 */
@DisplayName("LoginResponse DTO Tests")
class LoginResponseTest {

    @Test
    @DisplayName("Should create LoginResponse with default constructor")
    void testDefaultConstructor() {
        LoginResponse response = new LoginResponse();
        
        assertNotNull(response);
        assertEquals("Bearer", response.getType());
    }

    @Test
    @DisplayName("Should create LoginResponse with success data")
    void testSuccessConstructor() {
        LoginResponse response = new LoginResponse("jwt-token-123", 1L, "johndoe", "john@example.com");
        
        assertEquals("jwt-token-123", response.getToken());
        assertEquals(1L, response.getId());
        assertEquals("johndoe", response.getUsername());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("Login successful", response.getMessage());
        assertEquals("Bearer", response.getType());
    }

    @Test
    @DisplayName("Should create LoginResponse with error message")
    void testErrorConstructor() {
        LoginResponse response = new LoginResponse("Invalid credentials");
        
        assertEquals("Invalid credentials", response.getMessage());
        assertNull(response.getToken());
        assertNull(response.getId());
        assertNull(response.getUsername());
        assertNull(response.getEmail());
    }

    @Test
    @DisplayName("Should set and get token correctly")
    void testTokenGetterSetter() {
        LoginResponse response = new LoginResponse();
        response.setToken("new-token-456");
        
        assertEquals("new-token-456", response.getToken());
    }

    @Test
    @DisplayName("Should set and get type correctly")
    void testTypeGetterSetter() {
        LoginResponse response = new LoginResponse();
        response.setType("Custom");
        
        assertEquals("Custom", response.getType());
    }

    @Test
    @DisplayName("Should set and get id correctly")
    void testIdGetterSetter() {
        LoginResponse response = new LoginResponse();
        response.setId(100L);
        
        assertEquals(100L, response.getId());
    }

    @Test
    @DisplayName("Should set and get username correctly")
    void testUsernameGetterSetter() {
        LoginResponse response = new LoginResponse();
        response.setUsername("testuser");
        
        assertEquals("testuser", response.getUsername());
    }

    @Test
    @DisplayName("Should set and get email correctly")
    void testEmailGetterSetter() {
        LoginResponse response = new LoginResponse();
        response.setEmail("test@example.com");
        
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    @DisplayName("Should set and get message correctly")
    void testMessageGetterSetter() {
        LoginResponse response = new LoginResponse();
        response.setMessage("Custom message");
        
        assertEquals("Custom message", response.getMessage());
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullValues() {
        LoginResponse response = new LoginResponse();
        response.setToken(null);
        response.setId(null);
        response.setUsername(null);
        response.setEmail(null);
        response.setMessage(null);
        
        assertNull(response.getToken());
        assertNull(response.getId());
        assertNull(response.getUsername());
        assertNull(response.getEmail());
        assertNull(response.getMessage());
    }

    @Test
    @DisplayName("Should create response for successful authentication")
    void testSuccessfulAuthenticationResponse() {
        LoginResponse response = new LoginResponse(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            42L,
            "retirement_planner",
            "planner@finova.com"
        );
        
        assertNotNull(response.getToken());
        assertEquals(42L, response.getId());
        assertEquals("retirement_planner", response.getUsername());
        assertEquals("planner@finova.com", response.getEmail());
        assertEquals("Login successful", response.getMessage());
        assertEquals("Bearer", response.getType());
    }

    @Test
    @DisplayName("Should create response for failed authentication")
    void testFailedAuthenticationResponse() {
        LoginResponse response = new LoginResponse("Invalid username or password");
        
        assertEquals("Invalid username or password", response.getMessage());
        assertNull(response.getToken());
    }

    @Test
    @DisplayName("Should create response for disabled account")
    void testDisabledAccountResponse() {
        LoginResponse response = new LoginResponse("Account is disabled");
        
        assertEquals("Account is disabled", response.getMessage());
        assertNull(response.getToken());
    }
}
