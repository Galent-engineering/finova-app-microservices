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
 * Unit tests for RegisterRequest DTO
 */
@DisplayName("RegisterRequest DTO Tests")
class RegisterRequestTest {

    private Validator validator;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john.doe@example.com");
    }

    @Test
    @DisplayName("Should create RegisterRequest with default constructor")
    void testDefaultConstructor() {
        RegisterRequest request = new RegisterRequest();
        assertNotNull(request);
    }

    @Test
    @DisplayName("Should create RegisterRequest with parameterized constructor")
    void testParameterizedConstructor() {
        RegisterRequest request = new RegisterRequest(
            "johndoe", "securepass", "John", "Doe", "john@example.com"
        );
        
        assertEquals("johndoe", request.getUsername());
        assertEquals("securepass", request.getPassword());
        assertEquals("John", request.getFirstName());
        assertEquals("Doe", request.getLastName());
        assertEquals("john@example.com", request.getEmail());
    }

    @Test
    @DisplayName("Should validate RegisterRequest with valid data")
    void testValidRegisterRequest() {
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when username is blank")
    void testBlankUsername() {
        registerRequest.setUsername("");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when username is null")
    void testNullUsername() {
        registerRequest.setUsername(null);
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when username is too short")
    void testUsernameTooShort() {
        registerRequest.setUsername("ab");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when username is too long")
    void testUsernameTooLong() {
        registerRequest.setUsername("a".repeat(51));
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept username with minimum length")
    void testUsernameMinimumLength() {
        registerRequest.setUsername("abc");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept username with maximum length")
    void testUsernameMaximumLength() {
        registerRequest.setUsername("a".repeat(50));
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when password is blank")
    void testBlankPassword() {
        registerRequest.setPassword("");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when password is null")
    void testNullPassword() {
        registerRequest.setPassword(null);
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when password is too short")
    void testPasswordTooShort() {
        registerRequest.setPassword("12345");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept password with minimum length")
    void testPasswordMinimumLength() {
        registerRequest.setPassword("123456");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when first name is blank")
    void testBlankFirstName() {
        registerRequest.setFirstName("");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when first name is null")
    void testNullFirstName() {
        registerRequest.setFirstName(null);
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when last name is blank")
    void testBlankLastName() {
        registerRequest.setLastName("");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when last name is null")
    void testNullLastName() {
        registerRequest.setLastName(null);
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when email is invalid")
    void testInvalidEmail() {
        registerRequest.setEmail("invalid-email");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept valid email format")
    void testValidEmail() {
        registerRequest.setEmail("valid.email@example.com");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept null email")
    void testNullEmail() {
        registerRequest.setEmail(null);
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should set and get all fields correctly")
    void testAllGettersSetters() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("newpass123");
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setEmail("jane@example.com");
        
        assertEquals("newuser", request.getUsername());
        assertEquals("newpass123", request.getPassword());
        assertEquals("Jane", request.getFirstName());
        assertEquals("Smith", request.getLastName());
        assertEquals("jane@example.com", request.getEmail());
    }

    @Test
    @DisplayName("Should create valid registration for retirement planner")
    void testRetirementPlannerRegistration() {
        RegisterRequest request = new RegisterRequest(
            "retirement_planner",
            "SecurePass123!",
            "Robert",
            "Johnson",
            "robert.johnson@finova.com"
        );
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
        assertEquals("retirement_planner", request.getUsername());
        assertEquals("Robert", request.getFirstName());
        assertEquals("Johnson", request.getLastName());
    }
}
