package com.finova.user.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for User entity
 */
@DisplayName("User Model Tests")
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    @DisplayName("Should create user with default constructor")
    void testDefaultConstructor() {
        assertNotNull(user);
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertTrue(user.isEnabled());
        assertEquals("USER", user.getRole());
    }

    @Test
    @DisplayName("Should create user with parameterized constructor")
    void testParameterizedConstructor() {
        User newUser = new User("testuser", "password123", "John", "Doe", "john.doe@example.com");
        
        assertEquals("testuser", newUser.getUsername());
        assertEquals("password123", newUser.getPassword());
        assertEquals("John", newUser.getFirstName());
        assertEquals("Doe", newUser.getLastName());
        assertEquals("john.doe@example.com", newUser.getEmail());
        assertNotNull(newUser.getCreatedAt());
        assertNotNull(newUser.getUpdatedAt());
    }

    @Test
    @DisplayName("Should set and get user ID")
    void testSetAndGetId() {
        user.setId(1L);
        assertEquals(1L, user.getId());
    }

    @Test
    @DisplayName("Should set and get username")
    void testSetAndGetUsername() {
        user.setUsername("testuser");
        assertEquals("testuser", user.getUsername());
    }

    @Test
    @DisplayName("Should set and get password")
    void testSetAndGetPassword() {
        user.setPassword("securePassword123");
        assertEquals("securePassword123", user.getPassword());
    }

    @Test
    @DisplayName("Should set and get first name")
    void testSetAndGetFirstName() {
        user.setFirstName("Jane");
        assertEquals("Jane", user.getFirstName());
    }

    @Test
    @DisplayName("Should set and get last name")
    void testSetAndGetLastName() {
        user.setLastName("Smith");
        assertEquals("Smith", user.getLastName());
    }

    @Test
    @DisplayName("Should set and get email")
    void testSetAndGetEmail() {
        user.setEmail("jane.smith@example.com");
        assertEquals("jane.smith@example.com", user.getEmail());
    }

    @Test
    @DisplayName("Should set and get phone")
    void testSetAndGetPhone() {
        user.setPhone("555-1234");
        assertEquals("555-1234", user.getPhone());
    }

    @Test
    @DisplayName("Should set and get date of birth")
    void testSetAndGetDateOfBirth() {
        LocalDate dob = LocalDate.of(1985, 5, 15);
        user.setDateOfBirth(dob);
        assertEquals(dob, user.getDateOfBirth());
    }

    @Test
    @DisplayName("Should set and get annual salary")
    void testSetAndGetAnnualSalary() {
        BigDecimal salary = new BigDecimal("75000.00");
        user.setAnnualSalary(salary);
        assertEquals(salary, user.getAnnualSalary());
    }

    @Test
    @DisplayName("Should set and get employment status")
    void testSetAndGetEmploymentStatus() {
        user.setEmploymentStatus("Full-Time");
        assertEquals("Full-Time", user.getEmploymentStatus());
    }

    @Test
    @DisplayName("Should set and get years with employer")
    void testSetAndGetYearsWithEmployer() {
        user.setYearsWithEmployer(5);
        assertEquals(5, user.getYearsWithEmployer());
    }

    @Test
    @DisplayName("Should set and get marital status")
    void testSetAndGetMaritalStatus() {
        user.setMaritalStatus("Married");
        assertEquals("Married", user.getMaritalStatus());
    }

    @Test
    @DisplayName("Should set and get risk tolerance")
    void testSetAndGetRiskTolerance() {
        user.setRiskTolerance("Moderate");
        assertEquals("Moderate", user.getRiskTolerance());
    }

    @Test
    @DisplayName("Should set and get created at timestamp")
    void testSetAndGetCreatedAt() {
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        assertEquals(now, user.getCreatedAt());
    }

    @Test
    @DisplayName("Should set and get updated at timestamp")
    void testSetAndGetUpdatedAt() {
        LocalDateTime now = LocalDateTime.now();
        user.setUpdatedAt(now);
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    @DisplayName("Should set and get last login timestamp")
    void testSetAndGetLastLogin() {
        LocalDateTime now = LocalDateTime.now();
        user.setLastLogin(now);
        assertEquals(now, user.getLastLogin());
    }

    @Test
    @DisplayName("Should set and get enabled status")
    void testSetAndGetEnabled() {
        user.setEnabled(false);
        assertFalse(user.isEnabled());
        
        user.setEnabled(true);
        assertTrue(user.isEnabled());
    }

    @Test
    @DisplayName("Should set and get role")
    void testSetAndGetRole() {
        user.setRole("ADMIN");
        assertEquals("ADMIN", user.getRole());
    }

    @Test
    @DisplayName("Should return correct display name")
    void testGetDisplayName() {
        user.setFirstName("John");
        user.setLastName("Doe");
        
        assertEquals("John Doe", user.getDisplayName());
    }

    @Test
    @DisplayName("Should calculate age correctly from date of birth")
    void testGetAge() {
        LocalDate dob = LocalDate.of(1990, 1, 1);
        user.setDateOfBirth(dob);
        
        Integer age = user.getAge();
        assertNotNull(age);
        assertTrue(age >= 33); // Age will be at least 33 in 2023+
    }

    @Test
    @DisplayName("Should return null age when date of birth is not set")
    void testGetAgeWhenDateOfBirthIsNull() {
        user.setDateOfBirth(null);
        assertNull(user.getAge());
    }

    @Test
    @DisplayName("Should update timestamp on update")
    void testOnUpdate() {
        LocalDateTime originalUpdatedAt = user.getUpdatedAt();
        
        // Simulate PreUpdate callback
        user.onUpdate();
        
        assertNotNull(user.getUpdatedAt());
        assertTrue(user.getUpdatedAt().isAfter(originalUpdatedAt) || 
                   user.getUpdatedAt().isEqual(originalUpdatedAt));
    }

    @Test
    @DisplayName("Should handle financial profile data for retirement calculations")
    void testFinancialProfileData() {
        // Set up a complete financial profile
        user.setAnnualSalary(new BigDecimal("85000.00"));
        user.setEmploymentStatus("Full-Time");
        user.setYearsWithEmployer(10);
        user.setRiskTolerance("Moderate");
        user.setDateOfBirth(LocalDate.of(1980, 6, 15));
        
        // Verify all financial data is correctly stored
        assertEquals(new BigDecimal("85000.00"), user.getAnnualSalary());
        assertEquals("Full-Time", user.getEmploymentStatus());
        assertEquals(10, user.getYearsWithEmployer());
        assertEquals("Moderate", user.getRiskTolerance());
        assertNotNull(user.getAge());
    }

    @Test
    @DisplayName("Should handle user with minimal required fields")
    void testMinimalUserProfile() {
        User minimalUser = new User("minuser", "pass123", "Min", "User", "min@example.com");
        
        assertNotNull(minimalUser.getUsername());
        assertNotNull(minimalUser.getPassword());
        assertNotNull(minimalUser.getFirstName());
        assertNotNull(minimalUser.getLastName());
        assertNotNull(minimalUser.getEmail());
        assertNull(minimalUser.getPhone());
        assertNull(minimalUser.getDateOfBirth());
        assertNull(minimalUser.getAnnualSalary());
    }

    @Test
    @DisplayName("Should handle user with complete profile")
    void testCompleteUserProfile() {
        User completeUser = new User("completeuser", "secure123", "Complete", "User", "complete@example.com");
        completeUser.setPhone("555-9876");
        completeUser.setDateOfBirth(LocalDate.of(1985, 3, 20));
        completeUser.setAnnualSalary(new BigDecimal("95000.00"));
        completeUser.setEmploymentStatus("Full-Time");
        completeUser.setYearsWithEmployer(8);
        completeUser.setMaritalStatus("Single");
        completeUser.setRiskTolerance("Aggressive");
        
        assertNotNull(completeUser.getUsername());
        assertNotNull(completeUser.getPhone());
        assertNotNull(completeUser.getDateOfBirth());
        assertNotNull(completeUser.getAnnualSalary());
        assertNotNull(completeUser.getEmploymentStatus());
        assertNotNull(completeUser.getYearsWithEmployer());
        assertNotNull(completeUser.getMaritalStatus());
        assertNotNull(completeUser.getRiskTolerance());
    }
}
