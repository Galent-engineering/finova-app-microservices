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
    void shouldCreateLoginRequestWithDefaultConstructor() {
        LoginRequest request = new LoginRequest();
        
        assertThat(request).isNotNull();
        assertThat(request.getUsername()).isNull();
        assertThat(request.getPassword()).isNull();
    }

    @Test
    @DisplayName("Should create LoginRequest with parameterized constructor")
    void shouldCreateLoginRequestWithParameterizedConstructor() {
        LoginRequest request = new LoginRequest("johndoe", "securePass123");
        
        assertThat(request.getUsername()).isEqualTo("johndoe");
        assertThat(request.getPassword()).isEqualTo("securePass123");
    }

    @Test
    @DisplayName("Should validate LoginRequest with valid data")
    void shouldValidateLoginRequestWithValidData() {
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when username is blank")
    void shouldFailValidationWhenUsernameIsBlank() {
        loginRequest.setUsername("");
        
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getPropertyPath().toString().equals("username") &&
            v.getMessage().contains("Username is required")
        );
    }

    @Test
    @DisplayName("Should fail validation when username is null")
    void shouldFailValidationWhenUsernameIsNull() {
        loginRequest.setUsername(null);
        
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getPropertyPath().toString().equals("username")
        );
    }

    @Test
    @DisplayName("Should fail validation when password is blank")
    void shouldFailValidationWhenPasswordIsBlank() {
        loginRequest.setPassword("");
        
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getPropertyPath().toString().equals("password") &&
            v.getMessage().contains("Password is required")
        );
    }

    @Test
    @DisplayName("Should fail validation when password is null")
    void shouldFailValidationWhenPasswordIsNull() {
        loginRequest.setPassword(null);
        
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getPropertyPath().toString().equals("password")
        );
    }

    @Test
    @DisplayName("Should set and get username correctly")
    void shouldSetAndGetUsername() {
        loginRequest.setUsername("newuser");
        
        assertThat(loginRequest.getUsername()).isEqualTo("newuser");
    }

    @Test
    @DisplayName("Should set and get password correctly")
    void shouldSetAndGetPassword() {
        loginRequest.setPassword("newPassword456");
        
        assertThat(loginRequest.getPassword()).isEqualTo("newPassword456");
    }
}
