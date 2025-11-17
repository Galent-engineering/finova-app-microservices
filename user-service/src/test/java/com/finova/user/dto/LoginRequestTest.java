package com.finova.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LoginRequest DTO
 * Tests validation and data integrity
 */
@DisplayName("LoginRequest DTO Tests")
class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create LoginRequest with valid data")
    void testValidLoginRequest() {
        LoginRequest request = new LoginRequest("testuser", "password123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when username is blank")
    void testBlankUsername() {
        LoginRequest request = new LoginRequest("", "password123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    @DisplayName("Should fail validation when username is null")
    void testNullUsername() {
        LoginRequest request = new LoginRequest(null, "password123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    @DisplayName("Should fail validation when password is blank")
    void testBlankPassword() {
        LoginRequest request = new LoginRequest("testuser", "");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("Should fail validation when password is null")
    void testNullPassword() {
        LoginRequest request = new LoginRequest("testuser", null);

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("Should set and get username")
    void testSetAndGetUsername() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");

        assertEquals("testuser", request.getUsername());
    }

    @Test
    @DisplayName("Should set and get password")
    void testSetAndGetPassword() {
        LoginRequest request = new LoginRequest();
        request.setPassword("password123");

        assertEquals("password123", request.getPassword());
    }

    @Test
    @DisplayName("Should create LoginRequest with default constructor")
    void testDefaultConstructor() {
        LoginRequest request = new LoginRequest();

        assertNull(request.getUsername());
        assertNull(request.getPassword());
    }

    @Test
    @DisplayName("Should create LoginRequest with parameterized constructor")
    void testParameterizedConstructor() {
        LoginRequest request = new LoginRequest("testuser", "password123");

        assertEquals("testuser", request.getUsername());
        assertEquals("password123", request.getPassword());
    }

    @Test
    @DisplayName("Should handle whitespace-only username")
    void testWhitespaceUsername() {
        LoginRequest request = new LoginRequest("   ", "password123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should handle whitespace-only password")
    void testWhitespacePassword() {
        LoginRequest request = new LoginRequest("testuser", "   ");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept special characters in username")
    void testSpecialCharactersInUsername() {
        LoginRequest request = new LoginRequest("test.user+123@example.com", "password123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept special characters in password")
    void testSpecialCharactersInPassword() {
        LoginRequest request = new LoginRequest("testuser", "P@ssw0rd!#$");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept long username")
    void testLongUsername() {
        String longUsername = "a".repeat(100);
        LoginRequest request = new LoginRequest(longUsername, "password123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept long password")
    void testLongPassword() {
        String longPassword = "p".repeat(100);
        LoginRequest request = new LoginRequest("testuser", longPassword);

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
