package com.finova.user.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for User entity
 * Tests domain model behavior, validation constraints, and business logic
 */
@DisplayName("User Entity Tests")
class UserTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create user with valid data")
    void shouldCreateUserWithValidData() {
        // Given
        User user = new User("jsmith", "password123", "John", "Smith", "john.smith@example.com");

        // Then
        assertThat(user.getUsername()).isEqualTo("jsmith");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Smith");
        assertThat(user.getEmail()).isEqualTo("john.smith@example.com");
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.getRole()).isEqualTo("USER");
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should validate username is not blank")
    void shouldValidateUsernameNotBlank() {
        // Given
        User user = new User();
        user.setUsername("");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Smith");

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getPropertyPath().toString().equals("username") && 
            v.getMessage().contains("required")
        );
    }

    @Test
    @DisplayName("Should validate username minimum length")
    void shouldValidateUsernameMinLength() {
        // Given
        User user = new User();
        user.setUsername("ab"); // Less than 3 characters
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Smith");

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getPropertyPath().toString().equals("username")
        );
    }

    @Test
    @DisplayName("Should validate username maximum length")
    void shouldValidateUsernameMaxLength() {
        // Given
        User user = new User();
        user.setUsername("a".repeat(51)); // More than 50 characters
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Smith");

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getPropertyPath().toString().equals("username")
        );
    }

    @Test
    @DisplayName("Should validate email format")
    void shouldValidateEmailFormat() {
        // Given
        User user = new User("jsmith", "password123", "John", "Smith", "invalid-email");

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getPropertyPath().toString().equals("email") && 
            v.getMessage().contains("valid")
        );
    }

    @Test
    @DisplayName("Should accept valid email format")
    void shouldAcceptValidEmailFormat() {
        // Given
        User user = new User("jsmith", "password123", "John", "Smith", "john.smith@finova.com");

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertThat(violations).filteredOn(v -> v.getPropertyPath().toString().equals("email"))
            .isEmpty();
    }

    @Test
    @DisplayName("Should return correct display name")
    void shouldReturnCorrectDisplayName() {
        // Given
        User user = new User("jsmith", "password123", "John", "Smith", "john.smith@example.com");

        // When
        String displayName = user.getDisplayName();

        // Then
        assertThat(displayName).isEqualTo("John Smith");
    }

    @Test
    @DisplayName("Should calculate age from date of birth")
    void shouldCalculateAgeFromDateOfBirth() {
        // Given
        User user = new User();
        user.setDateOfBirth(LocalDate.now().minusYears(35));

        // When
        Integer age = user.getAge();

        // Then
        assertThat(age).isEqualTo(35);
    }

    @Test
    @DisplayName("Should return null age when date of birth is not set")
    void shouldReturnNullAgeWhenDateOfBirthNotSet() {
        // Given
        User user = new User();

        // When
        Integer age = user.getAge();

        // Then
        assertThat(age).isNull();
    }

    @Test
    @DisplayName("Should set and get all financial profile fields")
    void shouldSetAndGetAllFinancialProfileFields() {
        // Given
        User user = new User();
        BigDecimal salary = new BigDecimal("85000.00");
        
        // When
        user.setAnnualSalary(salary);
        user.setEmploymentStatus("FULL_TIME");
        user.setYearsWithEmployer(5);
        user.setMaritalStatus("MARRIED");
        user.setRiskTolerance("MODERATE");

        // Then
        assertThat(user.getAnnualSalary()).isEqualByComparingTo(salary);
        assertThat(user.getEmploymentStatus()).isEqualTo("FULL_TIME");
        assertThat(user.getYearsWithEmployer()).isEqualTo(5);
        assertThat(user.getMaritalStatus()).isEqualTo("MARRIED");
        assertThat(user.getRiskTolerance()).isEqualTo("MODERATE");
    }

    @Test
    @DisplayName("Should set and get contact information")
    void shouldSetAndGetContactInformation() {
        // Given
        User user = new User();
        
        // When
        user.setPhone("555-1234");
        user.setEmail("john.smith@finova.com");

        // Then
        assertThat(user.getPhone()).isEqualTo("555-1234");
        assertThat(user.getEmail()).isEqualTo("john.smith@finova.com");
    }

    @Test
    @DisplayName("Should initialize timestamps on creation")
    void shouldInitializeTimestampsOnCreation() {
        // Given
        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);
        
        // When
        User user = new User();
        
        // Then
        LocalDateTime afterCreation = LocalDateTime.now().plusSeconds(1);
        assertThat(user.getCreatedAt()).isBetween(beforeCreation, afterCreation);
        assertThat(user.getUpdatedAt()).isBetween(beforeCreation, afterCreation);
    }

    @Test
    @DisplayName("Should set last login timestamp")
    void shouldSetLastLoginTimestamp() {
        // Given
        User user = new User();
        LocalDateTime loginTime = LocalDateTime.now();
        
        // When
        user.setLastLogin(loginTime);

        // Then
        assertThat(user.getLastLogin()).isEqualTo(loginTime);
    }

    @Test
    @DisplayName("Should set and get enabled status")
    void shouldSetAndGetEnabledStatus() {
        // Given
        User user = new User();
        
        // When
        user.setEnabled(false);

        // Then
        assertThat(user.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Should set and get user role")
    void shouldSetAndGetUserRole() {
        // Given
        User user = new User();
        
        // When
        user.setRole("ADMIN");

        // Then
        assertThat(user.getRole()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Should default to USER role")
    void shouldDefaultToUserRole() {
        // Given & When
        User user = new User();

        // Then
        assertThat(user.getRole()).isEqualTo("USER");
    }

    @Test
    @DisplayName("Should default to enabled status")
    void shouldDefaultToEnabledStatus() {
        // Given & When
        User user = new User();

        // Then
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should handle retirement planning profile for young professional")
    void shouldHandleRetirementPlanningProfileForYoungProfessional() {
        // Given - Young professional scenario
        User user = new User("mjohnson", "password123", "Mary", "Johnson", "mary.j@finova.com");
        user.setDateOfBirth(LocalDate.now().minusYears(28));
        user.setAnnualSalary(new BigDecimal("65000.00"));
        user.setEmploymentStatus("FULL_TIME");
        user.setYearsWithEmployer(3);
        user.setMaritalStatus("SINGLE");
        user.setRiskTolerance("AGGRESSIVE");

        // Then
        assertThat(user.getAge()).isEqualTo(28);
        assertThat(user.getAnnualSalary()).isEqualByComparingTo(new BigDecimal("65000.00"));
        assertThat(user.getRiskTolerance()).isEqualTo("AGGRESSIVE");
    }

    @Test
    @DisplayName("Should handle retirement planning profile for mid-career professional")
    void shouldHandleRetirementPlanningProfileForMidCareerProfessional() {
        // Given - Mid-career professional scenario
        User user = new User("rthomas", "password123", "Robert", "Thomas", "robert.t@finova.com");
        user.setDateOfBirth(LocalDate.now().minusYears(45));
        user.setAnnualSalary(new BigDecimal("95000.00"));
        user.setEmploymentStatus("FULL_TIME");
        user.setYearsWithEmployer(12);
        user.setMaritalStatus("MARRIED");
        user.setRiskTolerance("MODERATE");

        // Then
        assertThat(user.getAge()).isEqualTo(45);
        assertThat(user.getAnnualSalary()).isEqualByComparingTo(new BigDecimal("95000.00"));
        assertThat(user.getRiskTolerance()).isEqualTo("MODERATE");
        assertThat(user.getYearsWithEmployer()).isEqualTo(12);
    }

    @Test
    @DisplayName("Should handle retirement planning profile for near-retirement professional")
    void shouldHandleRetirementPlanningProfileForNearRetirementProfessional() {
        // Given - Near-retirement professional scenario
        User user = new User("swilliams", "password123", "Susan", "Williams", "susan.w@finova.com");
        user.setDateOfBirth(LocalDate.now().minusYears(62));
        user.setAnnualSalary(new BigDecimal("110000.00"));
        user.setEmploymentStatus("FULL_TIME");
        user.setYearsWithEmployer(25);
        user.setMaritalStatus("MARRIED");
        user.setRiskTolerance("CONSERVATIVE");

        // Then
        assertThat(user.getAge()).isEqualTo(62);
        assertThat(user.getAnnualSalary()).isEqualByComparingTo(new BigDecimal("110000.00"));
        assertThat(user.getRiskTolerance()).isEqualTo("CONSERVATIVE");
        assertThat(user.getYearsWithEmployer()).isEqualTo(25);
    }
}
