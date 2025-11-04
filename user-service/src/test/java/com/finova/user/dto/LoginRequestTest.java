package com.finova.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LoginRequest DTO
 */
@DisplayName("LoginRequest DTO Tests")
class LoginRequestTest {

    private Validator validator;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
    }

    @Test
    @DisplayName("Should create LoginRequest with default constructor")
    void testDefaultConstructor() {
        LoginRequest request = new LoginRequest();
        assertNotNull(request);
    }

    @Test
    @DisplayName("Should create LoginRequest with parameterized constructor")
    void testParameterizedConstructor() {
        LoginRequest request = new LoginRequest("johndoe", "securepass");
        
        assertEquals("johndoe", request.getUsername());
        assertEquals("securepass", request.getPassword());
    }

    @Test
    @DisplayName("Should validate LoginRequest with valid data")
    void testValidLoginRequest() {
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when username is blank")
    void testBlankUsername() {
        loginRequest.setUsername("");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when username is null")
    void testNullUsername() {
        loginRequest.setUsername(null);
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when password is blank")
    void testBlankPassword() {
        loginRequest.setPassword("");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when password is null")
    void testNullPassword() {
        loginRequest.setPassword(null);
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should set and get username correctly")
    void testUsernameGetterSetter() {
        loginRequest.setUsername("newuser");
        assertEquals("newuser", loginRequest.getUsername());
    }

    @Test
    @DisplayName("Should set and get password correctly")
    void testPasswordGetterSetter() {
        loginRequest.setPassword("newpassword");
        assertEquals("newpassword", loginRequest.getPassword());
    }

    @Test
    @DisplayName("Should handle whitespace in username")
    void testWhitespaceUsername() {
        loginRequest.setUsername("   ");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should handle whitespace in password")
    void testWhitespacePassword() {
        loginRequest.setPassword("   ");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
    }
}
