package com.finova.user.validation;

import com.finova.user.dto.RegisterRequest;
import com.finova.user.dto.UserProfileDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validation tests for DTOs
 */
@SpringBootTest
@ActiveProfiles("test")
class ValidationTest {

    @Autowired
    private Validator validator;

    @Test
    void validateRegisterRequest_WithValidData_ShouldPass() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("Password123!");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");

        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void validateRegisterRequest_WithInvalidUsername_ShouldFail() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("ab"); // Too short
        request.setPassword("Password123!");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");

        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void validateRegisterRequest_WithInvalidPassword_ShouldFail() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("weak"); // Too weak
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");

        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void validateRegisterRequest_WithInvalidEmail_ShouldFail() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("Password123!");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("invalid-email"); // Invalid email

        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void validateUserProfileDTO_WithValidData_ShouldPass() {
        // Given
        UserProfileDTO profile = new UserProfileDTO();
        profile.setUsername("testuser");
        profile.setFirstName("John");
        profile.setLastName("Doe");
        profile.setEmail("john.doe@example.com");
        profile.setDateOfBirth(LocalDate.of(1980, 1, 1));
        profile.setAnnualSalary(java.math.BigDecimal.valueOf(75000));
        profile.setEmploymentStatus("EMPLOYED");
        profile.setMaritalStatus("SINGLE");
        profile.setRiskTolerance("MODERATE");

        // When
        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(profile);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void validateUserProfileDTO_WithInvalidEmploymentStatus_ShouldFail() {
        // Given
        UserProfileDTO profile = new UserProfileDTO();
        profile.setUsername("testuser");
        profile.setFirstName("John");
        profile.setLastName("Doe");
        profile.setEmail("john.doe@example.com");
        profile.setEmploymentStatus("INVALID_STATUS"); // Invalid status

        // When
        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(profile);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("employmentStatus")));
    }

    @Test
    void validateUserProfileDTO_WithInvalidRiskTolerance_ShouldFail() {
        // Given
        UserProfileDTO profile = new UserProfileDTO();
        profile.setUsername("testuser");
        profile.setFirstName("John");
        profile.setLastName("Doe");
        profile.setEmail("john.doe@example.com");
        profile.setRiskTolerance("INVALID_RISK"); // Invalid risk tolerance

        // When
        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(profile);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("riskTolerance")));
    }

    @Test
    void validateUserProfileDTO_WithFutureDateOfBirth_ShouldFail() {
        // Given
        UserProfileDTO profile = new UserProfileDTO();
        profile.setUsername("testuser");
        profile.setFirstName("John");
        profile.setLastName("Doe");
        profile.setEmail("john.doe@example.com");
        profile.setDateOfBirth(LocalDate.now().plusDays(1)); // Future date

        // When
        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(profile);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("dateOfBirth")));
    }

    @Test
    void validateUserProfileDTO_WithNegativeSalary_ShouldFail() {
        // Given
        UserProfileDTO profile = new UserProfileDTO();
        profile.setUsername("testuser");
        profile.setFirstName("John");
        profile.setLastName("Doe");
        profile.setEmail("john.doe@example.com");
        profile.setAnnualSalary(java.math.BigDecimal.valueOf(-1000)); // Negative salary

        // When
        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(profile);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("annualSalary")));
    }
}
