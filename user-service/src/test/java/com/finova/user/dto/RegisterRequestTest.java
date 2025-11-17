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
 * Unit tests for RegisterRequest DTO
 * Tests validation rules and data integrity
 */
@DisplayName("RegisterRequest DTO Tests")
class RegisterRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create RegisterRequest with valid data")
    void testValidRegisterRequest() {
        RegisterRequest request = new RegisterRequest("testuser", "password123", 
                "John", "Doe", "john.doe@example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when username is blank")
    void testBlankUsername() {
        RegisterRequest request = new RegisterRequest("", "password123", 
                "John", "Doe", "john.doe@example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    @DisplayName("Should fail validation when username is too short")
    void testUsernameTooShort() {
        RegisterRequest request = new RegisterRequest("ab", "password123", 
                "John", "Doe", "john.doe@example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    @DisplayName("Should fail validation when username is too long")
    void testUsernameTooLong() {
        String longUsername = "a".repeat(51);
        RegisterRequest request = new RegisterRequest(longUsername, "password123", 
                "John", "Doe", "john.doe@example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    @DisplayName("Should accept username with exactly 3 characters")
    void testUsernameMinLength() {
        RegisterRequest request = new RegisterRequest("abc", "password123", 
                "John", "Doe", "john.doe@example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept username with exactly 50 characters")
    void testUsernameMaxLength() {
        String username = "a".repeat(50);
        RegisterRequest request = new RegisterRequest(username, "password123", 
                "John", "Doe", "john.doe@example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when password is blank")
    void testBlankPassword() {
        RegisterRequest request = new RegisterRequest("testuser", "", 
                "John", "Doe", "john.doe@example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("Should fail validation when password is too short")
    void testPasswordTooShort() {
        RegisterRequest request = new RegisterRequest("testuser", "12345", 
                "John", "Doe", "john.doe@example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("Should accept password with exactly 6 characters")
    void testPasswordMinLength() {
        RegisterRequest request = new RegisterRequest("testuser", "123456", 
                "John", "Doe", "john.doe@example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when first name is blank")
    void testBlankFirstName() {
        RegisterRequest request = new RegisterRequest("testuser", "password123", 
                "", "Doe", "john.doe@example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
    }

    @Test
    @DisplayName("Should fail validation when last name is blank")
    void testBlankLastName() {
        RegisterRequest request = new RegisterRequest("testuser", "password123", 
                "John", "", "john.doe@example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("lastName")));
    }

    @Test
    @DisplayName("Should fail validation with invalid email format")
    void testInvalidEmailFormat() {
        RegisterRequest request = new RegisterRequest("testuser", "password123", 
                "John", "Doe", "invalid-email");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Should accept valid email format")
    void testValidEmailFormat() {
        RegisterRequest request = new RegisterRequest("testuser", "password123", 
                "John", "Doe", "john.doe@example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept null email")
    void testNullEmail() {
        RegisterRequest request = new RegisterRequest("testuser", "password123", 
                "John", "Doe", null);

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should create RegisterRequest with default constructor")
    void testDefaultConstructor() {
        RegisterRequest request = new RegisterRequest();

        assertNull(request.getUsername());
        assertNull(request.getPassword());
        assertNull(request.getFirstName());
        assertNull(request.getLastName());
        assertNull(request.getEmail());
    }

    @Test
    @DisplayName("Should create RegisterRequest with parameterized constructor")
    void testParameterizedConstructor() {
        RegisterRequest request = new RegisterRequest("testuser", "password123", 
                "John", "Doe", "john.doe@example.com");

        assertEquals("testuser", request.getUsername());
        assertEquals("password123", request.getPassword());
        assertEquals("John", request.getFirstName());
        assertEquals("Doe", request.getLastName());
        assertEquals("john.doe@example.com", request.getEmail());
    }

    @Test
    @DisplayName("Should set and get username")
    void testSetAndGetUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");

        assertEquals("testuser", request.getUsername());
    }

    @Test
    @DisplayName("Should set and get password")
    void testSetAndGetPassword() {
        RegisterRequest request = new RegisterRequest();
        request.setPassword("password123");

        assertEquals("password123", request.getPassword());
    }

    @Test
    @DisplayName("Should set and get first name")
    void testSetAndGetFirstName() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("John");

        assertEquals("John", request.getFirstName());
    }

    @Test
    @DisplayName("Should set and get last name")
    void testSetAndGetLastName() {
        RegisterRequest request = new RegisterRequest();
        request.setLastName("Doe");

        assertEquals("Doe", request.getLastName());
    }

    @Test
    @DisplayName("Should set and get email")
    void testSetAndGetEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("john.doe@example.com");

        assertEquals("john.doe@example.com", request.getEmail());
    }

    @Test
    @DisplayName("Should handle whitespace-only username")
    void testWhitespaceUsername() {
        RegisterRequest request = new RegisterRequest("   ", "password123", 
                "John", "Doe", "john.doe@example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should handle whitespace-only password")
    void testWhitespacePassword() {
        RegisterRequest request = new RegisterRequest("testuser", "   ", 
                "John", "Doe", "john.doe@example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should handle whitespace-only first name")
    void testWhitespaceFirstName() {
        RegisterRequest request = new RegisterRequest("testuser", "password123", 
                "   ", "Doe", "john.doe@example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should handle whitespace-only last name")
    void testWhitespaceLastName() {
        RegisterRequest request = new RegisterRequest("testuser", "password123", 
                "John", "   ", "john.doe@example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept special characters in password")
    void testSpecialCharactersInPassword() {
        RegisterRequest request = new RegisterRequest("testuser", "P@ssw0rd!#$", 
                "John", "Doe", "john.doe@example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept names with special characters")
    void testNamesWithSpecialCharacters() {
        RegisterRequest request = new RegisterRequest("testuser", "password123", 
                "Jean-Pierre", "O'Brien", "jean.pierre@example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept email with plus sign")
    void testEmailWithPlusSign() {
        RegisterRequest request = new RegisterRequest("testuser", "password123", 
                "John", "Doe", "john.doe+test@example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept email with subdomain")
    void testEmailWithSubdomain() {
        RegisterRequest request = new RegisterRequest("testuser", "password123", 
                "John", "Doe", "john.doe@mail.example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when all fields are null")
    void testAllFieldsNull() {
        RegisterRequest request = new RegisterRequest(null, null, null, null, null);

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 4); // username, password, firstName, lastName
    }

    @Test
    @DisplayName("Should accept long password")
    void testLongPassword() {
        String longPassword = "p".repeat(100);
        RegisterRequest request = new RegisterRequest("testuser", longPassword, 
                "John", "Doe", "john.doe@example.com");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
