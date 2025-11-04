package com.finova.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

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
    void shouldCreateRegisterRequestWithDefaultConstructor() {
        RegisterRequest request = new RegisterRequest();
        
        assertThat(request).isNotNull();
    }

    @Test
    @DisplayName("Should create RegisterRequest with parameterized constructor")
    void shouldCreateRegisterRequestWithParameterizedConstructor() {
        RegisterRequest request = new RegisterRequest(
            "johndoe", 
            "securePass123", 
            "John", 
            "Doe", 
            "john@example.com"
        );
        
        assertThat(request.getUsername()).isEqualTo("johndoe");
        assertThat(request.getPassword()).isEqualTo("securePass123");
        assertThat(request.getFirstName()).isEqualTo("John");
        assertThat(request.getLastName()).isEqualTo("Doe");
        assertThat(request.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("Should validate RegisterRequest with valid data")
    void shouldValidateRegisterRequestWithValidData() {
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when username is blank")
    void shouldFailValidationWhenUsernameIsBlank() {
        registerRequest.setUsername("");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getPropertyPath().toString().equals("username") &&
            v.getMessage().contains("Username is required")
        );
    }

    @Test
    @DisplayName("Should fail validation when username is too short")
    void shouldFailValidationWhenUsernameIsTooShort() {
        registerRequest.setUsername("ab");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getPropertyPath().toString().equals("username")
        );
    }

    @Test
    @DisplayName("Should fail validation when username is too long")
    void shouldFailValidationWhenUsernameIsTooLong() {
        registerRequest.setUsername("a".repeat(51));
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getPropertyPath().toString().equals("username")
        );
    }

    @Test
    @DisplayName("Should fail validation when password is blank")
    void shouldFailValidationWhenPasswordIsBlank() {
        registerRequest.setPassword("");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getPropertyPath().toString().equals("password") &&
            v.getMessage().contains("Password is required")
        );
    }

    @Test
    @DisplayName("Should fail validation when password is too short")
    void shouldFailValidationWhenPasswordIsTooShort() {
        registerRequest.setPassword("12345");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getPropertyPath().toString().equals("password") &&
            v.getMessage().contains("at least 6 characters")
        );
    }

    @Test
    @DisplayName("Should fail validation when first name is blank")
    void shouldFailValidationWhenFirstNameIsBlank() {
        registerRequest.setFirstName("");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getPropertyPath().toString().equals("firstName") &&
            v.getMessage().contains("First name is required")
        );
    }

    @Test
    @DisplayName("Should fail validation when last name is blank")
    void shouldFailValidationWhenLastNameIsBlank() {
        registerRequest.setLastName("");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getPropertyPath().toString().equals("lastName") &&
            v.getMessage().contains("Last name is required")
        );
    }

    @Test
    @DisplayName("Should fail validation when email is invalid")
    void shouldFailValidationWhenEmailIsInvalid() {
        registerRequest.setEmail("invalid-email");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getPropertyPath().toString().equals("email") &&
            v.getMessage().contains("Email should be valid")
        );
    }

    @Test
    @DisplayName("Should validate with valid email format")
    void shouldValidateWithValidEmailFormat() {
        registerRequest.setEmail("valid.email@finova.com");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should allow null email")
    void shouldAllowNullEmail() {
        registerRequest.setEmail(null);
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        
        // Should only fail on required fields, not email
        assertThat(violations).allMatch(v -> 
            !v.getPropertyPath().toString().equals("email")
        );
    }

    @Test
    @DisplayName("Should set and get all fields correctly")
    void shouldSetAndGetAllFieldsCorrectly() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("newpass123");
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setEmail("jane.smith@example.com");
        
        assertThat(request.getUsername()).isEqualTo("newuser");
        assertThat(request.getPassword()).isEqualTo("newpass123");
        assertThat(request.getFirstName()).isEqualTo("Jane");
        assertThat(request.getLastName()).isEqualTo("Smith");
        assertThat(request.getEmail()).isEqualTo("jane.smith@example.com");
    }
}
