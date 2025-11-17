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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for User entity
 * Tests validation, lifecycle methods, and computed properties
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
    void testCreateValidUser() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Valid user should have no violations");
    }

    @Test
    @DisplayName("Should fail validation when username is blank")
    void testUsernameNotBlank() {
        user.setUsername("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Blank username should cause validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    @DisplayName("Should fail validation when username is too short")
    void testUsernameTooShort() {
        user.setUsername("ab");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Username less than 3 characters should fail");
    }

    @Test
    @DisplayName("Should fail validation when username is too long")
    void testUsernameTooLong() {
        user.setUsername("a".repeat(51));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Username more than 50 characters should fail");
    }

    @Test
    @DisplayName("Should fail validation when password is blank")
    void testPasswordNotBlank() {
        user.setPassword("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Blank password should cause validation error");
    }

    @Test
    @DisplayName("Should fail validation when first name is blank")
    void testFirstNameNotBlank() {
        user.setFirstName("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Blank first name should cause validation error");
    }

    @Test
    @DisplayName("Should fail validation when last name is blank")
    void testLastNameNotBlank() {
        user.setLastName("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Blank last name should cause validation error");
    }

    @Test
    @DisplayName("Should fail validation with invalid email format")
    void testInvalidEmailFormat() {
        user.setEmail("invalid-email");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Invalid email format should cause validation error");
    }

    @Test
    @DisplayName("Should accept valid email format")
    void testValidEmailFormat() {
        user.setEmail("valid.email@example.com");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Valid email should pass validation");
    }

    @Test
    @DisplayName("Should initialize timestamps on creation")
    void testTimestampInitialization() {
        User newUser = new User();
        assertNotNull(newUser.getCreatedAt(), "Created timestamp should be initialized");
        assertNotNull(newUser.getUpdatedAt(), "Updated timestamp should be initialized");
    }

    @Test
    @DisplayName("Should set default values on creation")
    void testDefaultValues() {
        User newUser = new User();
        assertTrue(newUser.isEnabled(), "User should be enabled by default");
        assertEquals("USER", newUser.getRole(), "Default role should be USER");
    }

    @Test
    @DisplayName("Should create user with constructor parameters")
    void testConstructorWithParameters() {
        User newUser = new User("username", "password", "First", "Last", "email@test.com");
        
        assertEquals("username", newUser.getUsername());
        assertEquals("password", newUser.getPassword());
        assertEquals("First", newUser.getFirstName());
        assertEquals("Last", newUser.getLastName());
        assertEquals("email@test.com", newUser.getEmail());
        assertNotNull(newUser.getCreatedAt());
        assertNotNull(newUser.getUpdatedAt());
    }

    @Test
    @DisplayName("Should return correct display name")
    void testGetDisplayName() {
        user.setFirstName("John");
        user.setLastName("Doe");
        assertEquals("John Doe", user.getDisplayName());
    }

    @Test
    @DisplayName("Should calculate age from date of birth")
    void testGetAge() {
        LocalDate birthDate = LocalDate.now().minusYears(30);
        user.setDateOfBirth(birthDate);
        
        Integer age = user.getAge();
        assertNotNull(age);
        assertEquals(30, age);
    }

    @Test
    @DisplayName("Should return null age when date of birth is not set")
    void testGetAgeWhenDateOfBirthIsNull() {
        user.setDateOfBirth(null);
        assertNull(user.getAge(), "Age should be null when date of birth is not set");
    }

    @Test
    @DisplayName("Should set and get all financial fields")
    void testFinancialFields() {
        BigDecimal salary = new BigDecimal("75000.50");
        user.setAnnualSalary(salary);
        user.setEmploymentStatus("Full-time");
        user.setYearsWithEmployer(5);
        
        assertEquals(salary, user.getAnnualSalary());
        assertEquals("Full-time", user.getEmploymentStatus());
        assertEquals(5, user.getYearsWithEmployer());
    }

    @Test
    @DisplayName("Should set and get personal information fields")
    void testPersonalInformationFields() {
        user.setPhone("+1234567890");
        user.setMaritalStatus("Single");
        user.setRiskTolerance("Moderate");
        
        assertEquals("+1234567890", user.getPhone());
        assertEquals("Single", user.getMaritalStatus());
        assertEquals("Moderate", user.getRiskTolerance());
    }

    @Test
    @DisplayName("Should set and get last login timestamp")
    void testLastLogin() {
        LocalDateTime loginTime = LocalDateTime.now();
        user.setLastLogin(loginTime);
        assertEquals(loginTime, user.getLastLogin());
    }

    @Test
    @DisplayName("Should set and get enabled status")
    void testEnabledStatus() {
        user.setEnabled(false);
        assertFalse(user.isEnabled());
        
        user.setEnabled(true);
        assertTrue(user.isEnabled());
    }

    @Test
    @DisplayName("Should set and get role")
    void testRole() {
        user.setRole("ADMIN");
        assertEquals("ADMIN", user.getRole());
    }

    @Test
    @DisplayName("Should set and get user ID")
    void testUserId() {
        user.setId(123L);
        assertEquals(123L, user.getId());
    }

    @Test
    @DisplayName("Should handle null values for optional fields")
    void testNullOptionalFields() {
        user.setPhone(null);
        user.setDateOfBirth(null);
        user.setAnnualSalary(null);
        user.setEmploymentStatus(null);
        user.setYearsWithEmployer(null);
        user.setMaritalStatus(null);
        user.setRiskTolerance(null);
        
        assertNull(user.getPhone());
        assertNull(user.getDateOfBirth());
        assertNull(user.getAnnualSalary());
        assertNull(user.getEmploymentStatus());
        assertNull(user.getYearsWithEmployer());
        assertNull(user.getMaritalStatus());
        assertNull(user.getRiskTolerance());
    }

    @Test
    @DisplayName("Should handle BigDecimal precision for annual salary")
    void testAnnualSalaryPrecision() {
        BigDecimal salary = new BigDecimal("123456789012.99");
        user.setAnnualSalary(salary);
        assertEquals(salary, user.getAnnualSalary());
    }

    @Test
    @DisplayName("Should accept null email")
    void testNullEmail() {
        user.setEmail(null);
        assertNull(user.getEmail());
    }
}
