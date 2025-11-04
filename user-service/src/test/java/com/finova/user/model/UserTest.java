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
 * Tests validation, business logic, and entity behavior
 */
@DisplayName("User Entity Tests")
class UserTest {

    private Validator validator;
    private User user;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
    }

    @Test
    @DisplayName("Should create user with valid data")
    void shouldCreateUserWithValidData() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when username is blank")
    void shouldFailValidationWhenUsernameIsBlank() {
        user.setUsername("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("username"));
    }

    @Test
    @DisplayName("Should fail validation when username is too short")
    void shouldFailValidationWhenUsernameIsTooShort() {
        user.setUsername("ab");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail validation when username is too long")
    void shouldFailValidationWhenUsernameIsTooLong() {
        user.setUsername("a".repeat(51));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail validation when password is blank")
    void shouldFailValidationWhenPasswordIsBlank() {
        user.setPassword("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    @DisplayName("Should fail validation when first name is blank")
    void shouldFailValidationWhenFirstNameIsBlank() {
        user.setFirstName("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail validation when last name is blank")
    void shouldFailValidationWhenLastNameIsBlank() {
        user.setLastName("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail validation when email is invalid")
    void shouldFailValidationWhenEmailIsInvalid() {
        user.setEmail("invalid-email");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    @DisplayName("Should accept valid email format")
    void shouldAcceptValidEmailFormat() {
        user.setEmail("valid.email@example.com");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should initialize timestamps on creation")
    void shouldInitializeTimestampsOnCreation() {
        User newUser = new User();
        assertThat(newUser.getCreatedAt()).isNotNull();
        assertThat(newUser.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should set default enabled status to true")
    void shouldSetDefaultEnabledStatusToTrue() {
        User newUser = new User();
        assertThat(newUser.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should set default role to USER")
    void shouldSetDefaultRoleToUser() {
        User newUser = new User();
        assertThat(newUser.getRole()).isEqualTo("USER");
    }

    @Test
    @DisplayName("Should create user with constructor parameters")
    void shouldCreateUserWithConstructorParameters() {
        User newUser = new User("johndoe", "pass123", "John", "Doe", "john@example.com");
        
        assertThat(newUser.getUsername()).isEqualTo("johndoe");
        assertThat(newUser.getPassword()).isEqualTo("pass123");
        assertThat(newUser.getFirstName()).isEqualTo("John");
        assertThat(newUser.getLastName()).isEqualTo("Doe");
        assertThat(newUser.getEmail()).isEqualTo("john@example.com");
        assertThat(newUser.getCreatedAt()).isNotNull();
        assertThat(newUser.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should return correct display name")
    void shouldReturnCorrectDisplayName() {
        user.setFirstName("Jane");
        user.setLastName("Smith");
        assertThat(user.getDisplayName()).isEqualTo("Jane Smith");
    }

    @Test
    @DisplayName("Should calculate age from date of birth")
    void shouldCalculateAgeFromDateOfBirth() {
        LocalDate birthDate = LocalDate.now().minusYears(35);
        user.setDateOfBirth(birthDate);
        
        Integer age = user.getAge();
        assertThat(age).isEqualTo(35);
    }

    @Test
    @DisplayName("Should return null age when date of birth is not set")
    void shouldReturnNullAgeWhenDateOfBirthIsNotSet() {
        user.setDateOfBirth(null);
        assertThat(user.getAge()).isNull();
    }

    @Test
    @DisplayName("Should set and get all financial profile fields")
    void shouldSetAndGetAllFinancialProfileFields() {
        user.setAnnualSalary(new BigDecimal("75000.00"));
        user.setEmploymentStatus("FULL_TIME");
        user.setYearsWithEmployer(5);
        user.setMaritalStatus("MARRIED");
        user.setRiskTolerance("MODERATE");
        
        assertThat(user.getAnnualSalary()).isEqualByComparingTo(new BigDecimal("75000.00"));
        assertThat(user.getEmploymentStatus()).isEqualTo("FULL_TIME");
        assertThat(user.getYearsWithEmployer()).isEqualTo(5);
        assertThat(user.getMaritalStatus()).isEqualTo("MARRIED");
        assertThat(user.getRiskTolerance()).isEqualTo("MODERATE");
    }

    @Test
    @DisplayName("Should set and get phone number")
    void shouldSetAndGetPhoneNumber() {
        user.setPhone("555-1234");
        assertThat(user.getPhone()).isEqualTo("555-1234");
    }

    @Test
    @DisplayName("Should set and get last login timestamp")
    void shouldSetAndGetLastLoginTimestamp() {
        LocalDateTime lastLogin = LocalDateTime.now();
        user.setLastLogin(lastLogin);
        assertThat(user.getLastLogin()).isEqualTo(lastLogin);
    }

    @Test
    @DisplayName("Should allow disabling user account")
    void shouldAllowDisablingUserAccount() {
        user.setEnabled(false);
        assertThat(user.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Should allow setting custom role")
    void shouldAllowSettingCustomRole() {
        user.setRole("ADMIN");
        assertThat(user.getRole()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Should handle retirement planning profile data")
    void shouldHandleRetirementPlanningProfileData() {
        // Simulate a complete retirement planning profile
        user.setDateOfBirth(LocalDate.of(1980, 5, 15));
        user.setAnnualSalary(new BigDecimal("85000.00"));
        user.setEmploymentStatus("FULL_TIME");
        user.setYearsWithEmployer(10);
        user.setMaritalStatus("MARRIED");
        user.setRiskTolerance("MODERATE");
        
        assertThat(user.getAge()).isEqualTo(LocalDate.now().getYear() - 1980);
        assertThat(user.getAnnualSalary()).isNotNull();
        assertThat(user.getEmploymentStatus()).isNotNull();
        assertThat(user.getYearsWithEmployer()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should handle various risk tolerance levels")
    void shouldHandleVariousRiskToleranceLevels() {
        String[] riskLevels = {"CONSERVATIVE", "MODERATE", "AGGRESSIVE"};
        
        for (String level : riskLevels) {
            user.setRiskTolerance(level);
            assertThat(user.getRiskTolerance()).isEqualTo(level);
        }
    }

    @Test
    @DisplayName("Should handle various employment statuses")
    void shouldHandleVariousEmploymentStatuses() {
        String[] statuses = {"FULL_TIME", "PART_TIME", "SELF_EMPLOYED", "RETIRED", "UNEMPLOYED"};
        
        for (String status : statuses) {
            user.setEmploymentStatus(status);
            assertThat(user.getEmploymentStatus()).isEqualTo(status);
        }
    }

    @Test
    @DisplayName("Should handle various marital statuses")
    void shouldHandleVariousMaritalStatuses() {
        String[] statuses = {"SINGLE", "MARRIED", "DIVORCED", "WIDOWED"};
        
        for (String status : statuses) {
            user.setMaritalStatus(status);
            assertThat(user.getMaritalStatus()).isEqualTo(status);
        }
    }

    @Test
    @DisplayName("Should handle large salary values for high earners")
    void shouldHandleLargeSalaryValuesForHighEarners() {
        BigDecimal highSalary = new BigDecimal("500000.00");
        user.setAnnualSalary(highSalary);
        assertThat(user.getAnnualSalary()).isEqualByComparingTo(highSalary);
    }

    @Test
    @DisplayName("Should handle edge case of very long employment tenure")
    void shouldHandleEdgeCaseOfVeryLongEmploymentTenure() {
        user.setYearsWithEmployer(45);
        assertThat(user.getYearsWithEmployer()).isEqualTo(45);
    }

    @Test
    @DisplayName("Should handle user approaching retirement age")
    void shouldHandleUserApproachingRetirementAge() {
        LocalDate birthDate = LocalDate.now().minusYears(64);
        user.setDateOfBirth(birthDate);
        
        assertThat(user.getAge()).isEqualTo(64);
        assertThat(user.getAge()).isLessThan(67); // Typical retirement age
    }
}
