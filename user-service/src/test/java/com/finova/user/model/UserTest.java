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

    @Test
    @DisplayName("Should create user with default constructor")
    void testDefaultConstructor() {
        User newUser = new User();
        
        assertNotNull(newUser);
        assertNotNull(newUser.getCreatedAt());
        assertNotNull(newUser.getUpdatedAt());
        assertTrue(newUser.isEnabled());
        assertEquals("USER", newUser.getRole());
    }

    @Test
    @DisplayName("Should create user with parameterized constructor")
    void testParameterizedConstructor() {
        User newUser = new User("johndoe", "password123", "John", "Doe", "john@example.com");
        
        assertEquals("johndoe", newUser.getUsername());
        assertEquals("password123", newUser.getPassword());
        assertEquals("John", newUser.getFirstName());
        assertEquals("Doe", newUser.getLastName());
        assertEquals("john@example.com", newUser.getEmail());
        assertNotNull(newUser.getCreatedAt());
        assertNotNull(newUser.getUpdatedAt());
    }

    @Test
    @DisplayName("Should validate user with all required fields")
    void testValidUser() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when username is blank")
    void testBlankUsername() {
        user.setUsername("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when username is too short")
    void testUsernameTooShort() {
        user.setUsername("ab");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when username is too long")
    void testUsernameTooLong() {
        user.setUsername("a".repeat(51));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when password is blank")
    void testBlankPassword() {
        user.setPassword("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when first name is blank")
    void testBlankFirstName() {
        user.setFirstName("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when last name is blank")
    void testBlankLastName() {
        user.setLastName("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when email is invalid")
    void testInvalidEmail() {
        user.setEmail("invalid-email");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept valid email format")
    void testValidEmail() {
        user.setEmail("valid.email@example.com");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should get display name correctly")
    void testGetDisplayName() {
        user.setFirstName("Jane");
        user.setLastName("Smith");
        
        assertEquals("Jane Smith", user.getDisplayName());
    }

    @Test
    @DisplayName("Should calculate age correctly from date of birth")
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
        assertNull(user.getAge());
    }

    @Test
    @DisplayName("Should set and get all financial profile fields")
    void testFinancialProfileFields() {
        BigDecimal salary = new BigDecimal("75000.00");
        user.setAnnualSalary(salary);
        user.setEmploymentStatus("FULL_TIME");
        user.setYearsWithEmployer(5);
        user.setMaritalStatus("MARRIED");
        user.setRiskTolerance("MODERATE");
        
        assertEquals(salary, user.getAnnualSalary());
        assertEquals("FULL_TIME", user.getEmploymentStatus());
        assertEquals(5, user.getYearsWithEmployer());
        assertEquals("MARRIED", user.getMaritalStatus());
        assertEquals("MODERATE", user.getRiskTolerance());
    }

    @Test
    @DisplayName("Should set and get contact information")
    void testContactInformation() {
        user.setPhone("555-1234");
        LocalDate dob = LocalDate.of(1990, 5, 15);
        user.setDateOfBirth(dob);
        
        assertEquals("555-1234", user.getPhone());
        assertEquals(dob, user.getDateOfBirth());
    }

    @Test
    @DisplayName("Should set and get user status fields")
    void testUserStatusFields() {
        user.setEnabled(false);
        user.setRole("ADMIN");
        LocalDateTime lastLogin = LocalDateTime.now();
        user.setLastLogin(lastLogin);
        
        assertFalse(user.isEnabled());
        assertEquals("ADMIN", user.getRole());
        assertEquals(lastLogin, user.getLastLogin());
    }

    @Test
    @DisplayName("Should set and get ID")
    void testIdGetterSetter() {
        user.setId(123L);
        assertEquals(123L, user.getId());
    }

    @Test
    @DisplayName("Should handle timestamp fields correctly")
    void testTimestampFields() {
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    @DisplayName("Should handle null optional fields")
    void testNullOptionalFields() {
        user.setPhone(null);
        user.setDateOfBirth(null);
        user.setAnnualSalary(null);
        user.setEmploymentStatus(null);
        user.setYearsWithEmployer(null);
        user.setMaritalStatus(null);
        user.setRiskTolerance(null);
        user.setLastLogin(null);
        
        assertNull(user.getPhone());
        assertNull(user.getDateOfBirth());
        assertNull(user.getAnnualSalary());
        assertNull(user.getEmploymentStatus());
        assertNull(user.getYearsWithEmployer());
        assertNull(user.getMaritalStatus());
        assertNull(user.getRiskTolerance());
        assertNull(user.getLastLogin());
    }

    @Test
    @DisplayName("Should create user with realistic retirement planning data")
    void testRealisticRetirementPlanningData() {
        user.setDateOfBirth(LocalDate.of(1980, 3, 15));
        user.setAnnualSalary(new BigDecimal("85000.00"));
        user.setEmploymentStatus("FULL_TIME");
        user.setYearsWithEmployer(10);
        user.setMaritalStatus("MARRIED");
        user.setRiskTolerance("MODERATE");
        user.setPhone("555-0123");
        
        assertEquals(LocalDate.of(1980, 3, 15), user.getDateOfBirth());
        assertEquals(new BigDecimal("85000.00"), user.getAnnualSalary());
        assertEquals("FULL_TIME", user.getEmploymentStatus());
        assertEquals(10, user.getYearsWithEmployer());
        assertEquals("MARRIED", user.getMaritalStatus());
        assertEquals("MODERATE", user.getRiskTolerance());
        
        // Verify age calculation for retirement planning
        Integer age = user.getAge();
        assertNotNull(age);
        assertTrue(age >= 40 && age <= 50);
    }

    @Test
    @DisplayName("Should handle edge case for very young user")
    void testYoungUserAge() {
        user.setDateOfBirth(LocalDate.now().minusYears(18));
        assertEquals(18, user.getAge());
    }

    @Test
    @DisplayName("Should handle edge case for senior user")
    void testSeniorUserAge() {
        user.setDateOfBirth(LocalDate.now().minusYears(70));
        assertEquals(70, user.getAge());
    }
}
