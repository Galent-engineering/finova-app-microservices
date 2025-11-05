package com.finova.user.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LoginResponse DTO
 * Tests data integrity and constructor behavior
 */
@DisplayName("LoginResponse DTO Tests")
class LoginResponseTest {

    @Test
    @DisplayName("Should create LoginResponse with default constructor")
    void testDefaultConstructor() {
        LoginResponse response = new LoginResponse();

        assertNull(response.getToken());
        assertEquals("Bearer", response.getType());
        assertNull(response.getId());
        assertNull(response.getUsername());
        assertNull(response.getEmail());
        assertNull(response.getMessage());
    }

    @Test
    @DisplayName("Should create LoginResponse with success constructor")
    void testSuccessConstructor() {
        LoginResponse response = new LoginResponse("jwt-token", 1L, "testuser", "test@example.com");

        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Login successful", response.getMessage());
    }

    @Test
    @DisplayName("Should create LoginResponse with error message constructor")
    void testErrorMessageConstructor() {
        LoginResponse response = new LoginResponse("Invalid credentials");

        assertNull(response.getToken());
        assertEquals("Bearer", response.getType());
        assertNull(response.getId());
        assertNull(response.getUsername());
        assertNull(response.getEmail());
        assertEquals("Invalid credentials", response.getMessage());
    }

    @Test
    @DisplayName("Should set and get token")
    void testSetAndGetToken() {
        LoginResponse response = new LoginResponse();
        response.setToken("new-token");

        assertEquals("new-token", response.getToken());
    }

    @Test
    @DisplayName("Should set and get type")
    void testSetAndGetType() {
        LoginResponse response = new LoginResponse();
        response.setType("Custom");

        assertEquals("Custom", response.getType());
    }

    @Test
    @DisplayName("Should set and get id")
    void testSetAndGetId() {
        LoginResponse response = new LoginResponse();
        response.setId(123L);

        assertEquals(123L, response.getId());
    }

    @Test
    @DisplayName("Should set and get username")
    void testSetAndGetUsername() {
        LoginResponse response = new LoginResponse();
        response.setUsername("testuser");

        assertEquals("testuser", response.getUsername());
    }

    @Test
    @DisplayName("Should set and get email")
    void testSetAndGetEmail() {
        LoginResponse response = new LoginResponse();
        response.setEmail("test@example.com");

        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    @DisplayName("Should set and get message")
    void testSetAndGetMessage() {
        LoginResponse response = new LoginResponse();
        response.setMessage("Custom message");

        assertEquals("Custom message", response.getMessage());
    }

    @Test
    @DisplayName("Should handle null token")
    void testNullToken() {
        LoginResponse response = new LoginResponse();
        response.setToken(null);

        assertNull(response.getToken());
    }

    @Test
    @DisplayName("Should handle null id")
    void testNullId() {
        LoginResponse response = new LoginResponse();
        response.setId(null);

        assertNull(response.getId());
    }

    @Test
    @DisplayName("Should handle null username")
    void testNullUsername() {
        LoginResponse response = new LoginResponse();
        response.setUsername(null);

        assertNull(response.getUsername());
    }

    @Test
    @DisplayName("Should handle null email")
    void testNullEmail() {
        LoginResponse response = new LoginResponse();
        response.setEmail(null);

        assertNull(response.getEmail());
    }

    @Test
    @DisplayName("Should handle null message")
    void testNullMessage() {
        LoginResponse response = new LoginResponse();
        response.setMessage(null);

        assertNull(response.getMessage());
    }

    @Test
    @DisplayName("Should maintain default type value")
    void testDefaultTypeValue() {
        LoginResponse response = new LoginResponse();

        assertEquals("Bearer", response.getType());
    }

    @Test
    @DisplayName("Should create response for successful login")
    void testSuccessfulLoginResponse() {
        LoginResponse response = new LoginResponse("token123", 1L, "user1", "user1@test.com");

        assertNotNull(response.getToken());
        assertNotNull(response.getId());
        assertNotNull(response.getUsername());
        assertNotNull(response.getEmail());
        assertEquals("Login successful", response.getMessage());
    }

    @Test
    @DisplayName("Should create response for failed login")
    void testFailedLoginResponse() {
        LoginResponse response = new LoginResponse("Invalid username or password");

        assertNull(response.getToken());
        assertNull(response.getId());
        assertNull(response.getUsername());
        assertNull(response.getEmail());
        assertEquals("Invalid username or password", response.getMessage());
    }

    @Test
    @DisplayName("Should handle empty string values")
    void testEmptyStringValues() {
        LoginResponse response = new LoginResponse();
        response.setToken("");
        response.setUsername("");
        response.setEmail("");
        response.setMessage("");

        assertEquals("", response.getToken());
        assertEquals("", response.getUsername());
        assertEquals("", response.getEmail());
        assertEquals("", response.getMessage());
    }

    @Test
    @DisplayName("Should handle large ID values")
    void testLargeIdValue() {
        LoginResponse response = new LoginResponse();
        response.setId(Long.MAX_VALUE);

        assertEquals(Long.MAX_VALUE, response.getId());
    }

    @Test
    @DisplayName("Should handle zero ID value")
    void testZeroIdValue() {
        LoginResponse response = new LoginResponse();
        response.setId(0L);

        assertEquals(0L, response.getId());
    }

    @Test
    @DisplayName("Should handle negative ID value")
    void testNegativeIdValue() {
        LoginResponse response = new LoginResponse();
        response.setId(-1L);

        assertEquals(-1L, response.getId());
    }

    @Test
    @DisplayName("Should handle special characters in message")
    void testSpecialCharactersInMessage() {
        String specialMessage = "Error: Invalid credentials! @#$%^&*()";
        LoginResponse response = new LoginResponse(specialMessage);

        assertEquals(specialMessage, response.getMessage());
    }

    @Test
    @DisplayName("Should handle long token string")
    void testLongTokenString() {
        String longToken = "a".repeat(1000);
        LoginResponse response = new LoginResponse();
        response.setToken(longToken);

        assertEquals(longToken, response.getToken());
    }
}
