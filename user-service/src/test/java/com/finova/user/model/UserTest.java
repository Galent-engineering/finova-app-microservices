package com.finova.user.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

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
 * Comprehensive unit tests for User entity
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
        user.setPassword("encodedPassword123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create user with default constructor and set timestamps")
        void shouldCreateUserWithDefaultConstructor() {
            User newUser = new User();
            
            assertThat(newUser.getCreatedAt()).isNotNull();
            assertThat(newUser.getUpdatedAt()).isNotNull();
            assertThat(newUser.isEnabled()).isTrue();
            assertThat(newUser.getRole()).isEqualTo("USER");
        }

        @Test
        @DisplayName("Should create user with parameterized constructor")
        void shouldCreateUserWithParameterizedConstructor() {
            User newUser = new User("johndoe", "password123", "John", "Doe", "john@example.com");
            
            assertThat(newUser.getUsername()).isEqualTo("johndoe");
            assertThat(newUser.getPassword()).isEqualTo("password123");
            assertThat(newUser.getFirstName()).isEqualTo("John");
            assertThat(newUser.getLastName()).isEqualTo("Doe");
            assertThat(newUser.getEmail()).isEqualTo("john@example.com");
            assertThat(newUser.getCreatedAt()).isNotNull();
            assertThat(newUser.getUpdatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should validate user with all required fields")
        void shouldValidateUserWithRequiredFields() {
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when username is blank")
        void shouldFailValidationWhenUsernameIsBlank() {
            user.setUsername("");
            
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("username") &&
                v.getMessage().contains("Username is required")
            );
        }

        @Test
        @DisplayName("Should fail validation when username is too short")
        void shouldFailValidationWhenUsernameIsTooShort() {
            user.setUsername("ab");
            
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("username")
            );
        }

        @Test
        @DisplayName("Should fail validation when username is too long")
        void shouldFailValidationWhenUsernameIsTooLong() {
            user.setUsername("a".repeat(51));
            
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("username")
            );
        }

        @Test
        @DisplayName("Should fail validation when password is blank")
        void shouldFailValidationWhenPasswordIsBlank() {
            user.setPassword("");
            
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("password") &&
                v.getMessage().contains("Password is required")
            );
        }

        @Test
        @DisplayName("Should fail validation when first name is blank")
        void shouldFailValidationWhenFirstNameIsBlank() {
            user.setFirstName("");
            
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("firstName") &&
                v.getMessage().contains("First name is required")
            );
        }

        @Test
        @DisplayName("Should fail validation when last name is blank")
        void shouldFailValidationWhenLastNameIsBlank() {
            user.setLastName("");
            
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("lastName") &&
                v.getMessage().contains("Last name is required")
            );
        }

        @Test
        @DisplayName("Should fail validation when email is invalid")
        void shouldFailValidationWhenEmailIsInvalid() {
            user.setEmail("invalid-email");
            
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> 
                v.getPropertyPath().toString().equals("email") &&
                v.getMessage().contains("Email should be valid")
            );
        }

        @Test
        @DisplayName("Should validate user with valid email format")
        void shouldValidateUserWithValidEmail() {
            user.setEmail("valid.email@example.com");
            
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should return correct display name")
        void shouldReturnCorrectDisplayName() {
            user.setFirstName("John");
            user.setLastName("Doe");
            
            String displayName = user.getDisplayName();
            
            assertThat(displayName).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("Should calculate age correctly from date of birth")
        void shouldCalculateAgeCorrectly() {
            LocalDate birthDate = LocalDate.now().minusYears(35);
            user.setDateOfBirth(birthDate);
            
            Integer age = user.getAge();
            
            assertThat(age).isEqualTo(35);
        }

        @Test
        @DisplayName("Should return null age when date of birth is not set")
        void shouldReturnNullAgeWhenDateOfBirthIsNull() {
            user.setDateOfBirth(null);
            
            Integer age = user.getAge();
            
            assertThat(age).isNull();
        }

        @Test
        @DisplayName("Should handle retirement age calculation for financial planning")
        void shouldHandleRetirementAgeCalculation() {
            // Test for user approaching retirement age (65)
            LocalDate birthDate = LocalDate.now().minusYears(62);
            user.setDateOfBirth(birthDate);
            
            Integer age = user.getAge();
            Integer yearsToRetirement = 65 - age;
            
            assertThat(age).isEqualTo(62);
            assertThat(yearsToRetirement).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("Financial Profile Tests")
    class FinancialProfileTests {

        @Test
        @DisplayName("Should set and get annual salary correctly")
        void shouldSetAndGetAnnualSalary() {
            BigDecimal salary = new BigDecimal("75000.00");
            user.setAnnualSalary(salary);
            
            assertThat(user.getAnnualSalary()).isEqualByComparingTo(salary);
        }

        @Test
        @DisplayName("Should handle large salary amounts for high earners")
        void shouldHandleLargeSalaryAmounts() {
            BigDecimal largeSalary = new BigDecimal("500000.50");
            user.setAnnualSalary(largeSalary);
            
            assertThat(user.getAnnualSalary()).isEqualByComparingTo(largeSalary);
        }

        @Test
        @DisplayName("Should set employment status correctly")
        void shouldSetEmploymentStatus() {
            user.setEmploymentStatus("FULL_TIME");
            
            assertThat(user.getEmploymentStatus()).isEqualTo("FULL_TIME");
        }

        @Test
        @DisplayName("Should set years with employer for vesting calculations")
        void shouldSetYearsWithEmployer() {
            user.setYearsWithEmployer(5);
            
            assertThat(user.getYearsWithEmployer()).isEqualTo(5);
        }

        @Test
        @DisplayName("Should set risk tolerance for investment strategy")
        void shouldSetRiskTolerance() {
            user.setRiskTolerance("MODERATE");
            
            assertThat(user.getRiskTolerance()).isEqualTo("MODERATE");
        }

        @Test
        @DisplayName("Should set marital status for tax calculations")
        void shouldSetMaritalStatus() {
            user.setMaritalStatus("MARRIED");
            
            assertThat(user.getMaritalStatus()).isEqualTo("MARRIED");
        }
    }

    @Nested
    @DisplayName("Timestamp Tests")
    class TimestampTests {

        @Test
        @DisplayName("Should set created timestamp on instantiation")
        void shouldSetCreatedTimestamp() {
            User newUser = new User();
            
            assertThat(newUser.getCreatedAt()).isNotNull();
            assertThat(newUser.getCreatedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
        }

        @Test
        @DisplayName("Should set updated timestamp on instantiation")
        void shouldSetUpdatedTimestamp() {
            User newUser = new User();
            
            assertThat(newUser.getUpdatedAt()).isNotNull();
            assertThat(newUser.getUpdatedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
        }

        @Test
        @DisplayName("Should update last login timestamp")
        void shouldUpdateLastLoginTimestamp() {
            LocalDateTime loginTime = LocalDateTime.now();
            user.setLastLogin(loginTime);
            
            assertThat(user.getLastLogin()).isEqualTo(loginTime);
        }
    }

    @Nested
    @DisplayName("Security and Access Tests")
    class SecurityTests {

        @Test
        @DisplayName("Should set user as enabled by default")
        void shouldSetUserAsEnabledByDefault() {
            User newUser = new User();
            
            assertThat(newUser.isEnabled()).isTrue();
        }

        @Test
        @DisplayName("Should allow disabling user account")
        void shouldAllowDisablingUserAccount() {
            user.setEnabled(false);
            
            assertThat(user.isEnabled()).isFalse();
        }

        @Test
        @DisplayName("Should set default role as USER")
        void shouldSetDefaultRoleAsUser() {
            User newUser = new User();
            
            assertThat(newUser.getRole()).isEqualTo("USER");
        }

        @Test
        @DisplayName("Should allow setting admin role")
        void shouldAllowSettingAdminRole() {
            user.setRole("ADMIN");
            
            assertThat(user.getRole()).isEqualTo("ADMIN");
        }
    }

    @Nested
    @DisplayName("Contact Information Tests")
    class ContactInformationTests {

        @Test
        @DisplayName("Should set and get phone number")
        void shouldSetAndGetPhoneNumber() {
            user.setPhone("555-1234");
            
            assertThat(user.getPhone()).isEqualTo("555-1234");
        }

        @Test
        @DisplayName("Should set and get email address")
        void shouldSetAndGetEmail() {
            user.setEmail("newemail@example.com");
            
            assertThat(user.getEmail()).isEqualTo("newemail@example.com");
        }

        @Test
        @DisplayName("Should handle null phone number")
        void shouldHandleNullPhoneNumber() {
            user.setPhone(null);
            
            assertThat(user.getPhone()).isNull();
        }
    }

    @Nested
    @DisplayName("Complete User Profile Tests")
    class CompleteProfileTests {

        @Test
        @DisplayName("Should create complete user profile for retirement planning")
        void shouldCreateCompleteUserProfile() {
            User completeUser = new User("jsmith", "hashedPassword", "Jane", "Smith", "jane.smith@example.com");
            completeUser.setPhone("555-9876");
            completeUser.setDateOfBirth(LocalDate.of(1980, 5, 15));
            completeUser.setAnnualSalary(new BigDecimal("85000.00"));
            completeUser.setEmploymentStatus("FULL_TIME");
            completeUser.setYearsWithEmployer(10);
            completeUser.setMaritalStatus("MARRIED");
            completeUser.setRiskTolerance("MODERATE");
            completeUser.setEnabled(true);
            completeUser.setRole("USER");
            
            // Verify all fields
            assertThat(completeUser.getUsername()).isEqualTo("jsmith");
            assertThat(completeUser.getFirstName()).isEqualTo("Jane");
            assertThat(completeUser.getLastName()).isEqualTo("Smith");
            assertThat(completeUser.getEmail()).isEqualTo("jane.smith@example.com");
            assertThat(completeUser.getPhone()).isEqualTo("555-9876");
            assertThat(completeUser.getDateOfBirth()).isEqualTo(LocalDate.of(1980, 5, 15));
            assertThat(completeUser.getAnnualSalary()).isEqualByComparingTo(new BigDecimal("85000.00"));
            assertThat(completeUser.getEmploymentStatus()).isEqualTo("FULL_TIME");
            assertThat(completeUser.getYearsWithEmployer()).isEqualTo(10);
            assertThat(completeUser.getMaritalStatus()).isEqualTo("MARRIED");
            assertThat(completeUser.getRiskTolerance()).isEqualTo("MODERATE");
            assertThat(completeUser.isEnabled()).isTrue();
            assertThat(completeUser.getRole()).isEqualTo("USER");
            assertThat(completeUser.getDisplayName()).isEqualTo("Jane Smith");
            
            // Verify age calculation
            Integer expectedAge = LocalDate.now().getYear() - 1980;
            assertThat(completeUser.getAge()).isEqualTo(expectedAge);
        }
    }
}
