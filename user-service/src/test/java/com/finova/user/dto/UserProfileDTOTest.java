package com.finova.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserProfileDTO
 */
@DisplayName("UserProfileDTO Tests")
class UserProfileDTOTest {

    private Validator validator;
    private UserProfileDTO profileDTO;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        profileDTO = new UserProfileDTO();
        profileDTO.setUsername("testuser");
        profileDTO.setFirstName("John");
        profileDTO.setLastName("Doe");
        profileDTO.setEmail("john.doe@example.com");
    }

    @Test
    @DisplayName("Should create UserProfileDTO with default constructor")
    void testDefaultConstructor() {
        UserProfileDTO dto = new UserProfileDTO();
        assertNotNull(dto);
    }

    @Test
    @DisplayName("Should create UserProfileDTO with parameterized constructor")
    void testParameterizedConstructor() {
        UserProfileDTO dto = new UserProfileDTO("johndoe", "John", "Doe", "john@example.com");
        
        assertEquals("johndoe", dto.getUsername());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("john@example.com", dto.getEmail());
    }

    @Test
    @DisplayName("Should validate UserProfileDTO with valid data")
    void testValidUserProfileDTO() {
        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(profileDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when username is blank")
    void testBlankUsername() {
        profileDTO.setUsername("");
        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(profileDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when username is too short")
    void testUsernameTooShort() {
        profileDTO.setUsername("ab");
        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(profileDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when username is too long")
    void testUsernameTooLong() {
        profileDTO.setUsername("a".repeat(51));
        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(profileDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when first name is blank")
    void testBlankFirstName() {
        profileDTO.setFirstName("");
        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(profileDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when last name is blank")
    void testBlankLastName() {
        profileDTO.setLastName("");
        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(profileDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when email is invalid")
    void testInvalidEmail() {
        profileDTO.setEmail("invalid-email");
        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(profileDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept valid email format")
    void testValidEmail() {
        profileDTO.setEmail("valid.email@example.com");
        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(profileDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should get display name correctly")
    void testGetDisplayName() {
        profileDTO.setFirstName("Jane");
        profileDTO.setLastName("Smith");
        
        assertEquals("Jane Smith", profileDTO.getDisplayName());
    }

    @Test
    @DisplayName("Should set and get ID")
    void testIdGetterSetter() {
        profileDTO.setId(123L);
        assertEquals(123L, profileDTO.getId());
    }

    @Test
    @DisplayName("Should set and get phone")
    void testPhoneGetterSetter() {
        profileDTO.setPhone("555-1234");
        assertEquals("555-1234", profileDTO.getPhone());
    }

    @Test
    @DisplayName("Should set and get date of birth")
    void testDateOfBirthGetterSetter() {
        LocalDate dob = LocalDate.of(1990, 5, 15);
        profileDTO.setDateOfBirth(dob);
        assertEquals(dob, profileDTO.getDateOfBirth());
    }

    @Test
    @DisplayName("Should set and get annual salary")
    void testAnnualSalaryGetterSetter() {
        BigDecimal salary = new BigDecimal("75000.00");
        profileDTO.setAnnualSalary(salary);
        assertEquals(salary, profileDTO.getAnnualSalary());
    }

    @Test
    @DisplayName("Should set and get employment status")
    void testEmploymentStatusGetterSetter() {
        profileDTO.setEmploymentStatus("FULL_TIME");
        assertEquals("FULL_TIME", profileDTO.getEmploymentStatus());
    }

    @Test
    @DisplayName("Should set and get years with employer")
    void testYearsWithEmployerGetterSetter() {
        profileDTO.setYearsWithEmployer(10);
        assertEquals(10, profileDTO.getYearsWithEmployer());
    }

    @Test
    @DisplayName("Should set and get marital status")
    void testMaritalStatusGetterSetter() {
        profileDTO.setMaritalStatus("MARRIED");
        assertEquals("MARRIED", profileDTO.getMaritalStatus());
    }

    @Test
    @DisplayName("Should set and get risk tolerance")
    void testRiskToleranceGetterSetter() {
        profileDTO.setRiskTolerance("MODERATE");
        assertEquals("MODERATE", profileDTO.getRiskTolerance());
    }

    @Test
    @DisplayName("Should handle null optional fields")
    void testNullOptionalFields() {
        profileDTO.setPhone(null);
        profileDTO.setDateOfBirth(null);
        profileDTO.setAnnualSalary(null);
        profileDTO.setEmploymentStatus(null);
        profileDTO.setYearsWithEmployer(null);
        profileDTO.setMaritalStatus(null);
        profileDTO.setRiskTolerance(null);
        
        assertNull(profileDTO.getPhone());
        assertNull(profileDTO.getDateOfBirth());
        assertNull(profileDTO.getAnnualSalary());
        assertNull(profileDTO.getEmploymentStatus());
        assertNull(profileDTO.getYearsWithEmployer());
        assertNull(profileDTO.getMaritalStatus());
        assertNull(profileDTO.getRiskTolerance());
    }

    @Test
    @DisplayName("Should create complete retirement planning profile")
    void testCompleteRetirementProfile() {
        UserProfileDTO dto = new UserProfileDTO("retirement_user", "Alice", "Johnson", "alice@finova.com");
        dto.setId(42L);
        dto.setPhone("555-9876");
        dto.setDateOfBirth(LocalDate.of(1975, 8, 20));
        dto.setAnnualSalary(new BigDecimal("95000.00"));
        dto.setEmploymentStatus("FULL_TIME");
        dto.setYearsWithEmployer(15);
        dto.setMaritalStatus("MARRIED");
        dto.setRiskTolerance("MODERATE");
        
        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
        
        assertEquals(42L, dto.getId());
        assertEquals("retirement_user", dto.getUsername());
        assertEquals("Alice", dto.getFirstName());
        assertEquals("Johnson", dto.getLastName());
        assertEquals("alice@finova.com", dto.getEmail());
        assertEquals("555-9876", dto.getPhone());
        assertEquals(LocalDate.of(1975, 8, 20), dto.getDateOfBirth());
        assertEquals(new BigDecimal("95000.00"), dto.getAnnualSalary());
        assertEquals("FULL_TIME", dto.getEmploymentStatus());
        assertEquals(15, dto.getYearsWithEmployer());
        assertEquals("MARRIED", dto.getMaritalStatus());
        assertEquals("MODERATE", dto.getRiskTolerance());
        assertEquals("Alice Johnson", dto.getDisplayName());
    }

    @Test
    @DisplayName("Should handle conservative risk tolerance profile")
    void testConservativeRiskProfile() {
        profileDTO.setRiskTolerance("CONSERVATIVE");
        profileDTO.setAnnualSalary(new BigDecimal("60000.00"));
        profileDTO.setYearsWithEmployer(20);
        
        assertEquals("CONSERVATIVE", profileDTO.getRiskTolerance());
        assertEquals(new BigDecimal("60000.00"), profileDTO.getAnnualSalary());
        assertEquals(20, profileDTO.getYearsWithEmployer());
    }

    @Test
    @DisplayName("Should handle aggressive risk tolerance profile")
    void testAggressiveRiskProfile() {
        profileDTO.setRiskTolerance("AGGRESSIVE");
        profileDTO.setAnnualSalary(new BigDecimal("120000.00"));
        profileDTO.setYearsWithEmployer(5);
        
        assertEquals("AGGRESSIVE", profileDTO.getRiskTolerance());
        assertEquals(new BigDecimal("120000.00"), profileDTO.getAnnualSalary());
        assertEquals(5, profileDTO.getYearsWithEmployer());
    }

    @Test
    @DisplayName("Should handle various employment statuses")
    void testVariousEmploymentStatuses() {
        String[] statuses = {"FULL_TIME", "PART_TIME", "SELF_EMPLOYED", "RETIRED", "UNEMPLOYED"};
        
        for (String status : statuses) {
            profileDTO.setEmploymentStatus(status);
            assertEquals(status, profileDTO.getEmploymentStatus());
        }
    }

    @Test
    @DisplayName("Should handle various marital statuses")
    void testVariousMaritalStatuses() {
        String[] statuses = {"SINGLE", "MARRIED", "DIVORCED", "WIDOWED"};
        
        for (String status : statuses) {
            profileDTO.setMaritalStatus(status);
            assertEquals(status, profileDTO.getMaritalStatus());
        }
    }

    @Test
    @DisplayName("Should handle large salary values")
    void testLargeSalaryValues() {
        BigDecimal largeSalary = new BigDecimal("999999.99");
        profileDTO.setAnnualSalary(largeSalary);
        assertEquals(largeSalary, profileDTO.getAnnualSalary());
    }

    @Test
    @DisplayName("Should handle zero years with employer")
    void testZeroYearsWithEmployer() {
        profileDTO.setYearsWithEmployer(0);
        assertEquals(0, profileDTO.getYearsWithEmployer());
    }

    @Test
    @DisplayName("Should handle many years with employer")
    void testManyYearsWithEmployer() {
        profileDTO.setYearsWithEmployer(40);
        assertEquals(40, profileDTO.getYearsWithEmployer());
    }
}
