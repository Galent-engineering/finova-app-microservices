package com.finova.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserProfileDTO
 * Tests validation, data integrity, and computed properties
 */
@DisplayName("UserProfileDTO Tests")
class UserProfileDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create UserProfileDTO with valid data")
    void testValidUserProfileDTO() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername("testuser");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");

        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when username is blank")
    void testBlankUsername() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername("");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");

        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    @DisplayName("Should fail validation when username is too short")
    void testUsernameTooShort() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername("ab");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");

        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when username is too long")
    void testUsernameTooLong() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername("a".repeat(51));
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");

        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when first name is blank")
    void testBlankFirstName() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername("testuser");
        dto.setFirstName("");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");

        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
    }

    @Test
    @DisplayName("Should fail validation when last name is blank")
    void testBlankLastName() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername("testuser");
        dto.setFirstName("John");
        dto.setLastName("");
        dto.setEmail("john.doe@example.com");

        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("lastName")));
    }

    @Test
    @DisplayName("Should fail validation with invalid email format")
    void testInvalidEmailFormat() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername("testuser");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("invalid-email");

        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Should accept valid email format")
    void testValidEmailFormat() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername("testuser");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");

        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should create UserProfileDTO with default constructor")
    void testDefaultConstructor() {
        UserProfileDTO dto = new UserProfileDTO();

        assertNull(dto.getId());
        assertNull(dto.getUsername());
        assertNull(dto.getFirstName());
        assertNull(dto.getLastName());
        assertNull(dto.getEmail());
        assertNull(dto.getPhone());
        assertNull(dto.getDateOfBirth());
        assertNull(dto.getAnnualSalary());
        assertNull(dto.getEmploymentStatus());
        assertNull(dto.getYearsWithEmployer());
        assertNull(dto.getMaritalStatus());
        assertNull(dto.getRiskTolerance());
    }

    @Test
    @DisplayName("Should create UserProfileDTO with parameterized constructor")
    void testParameterizedConstructor() {
        UserProfileDTO dto = new UserProfileDTO("testuser", "John", "Doe", "john.doe@example.com");

        assertEquals("testuser", dto.getUsername());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("john.doe@example.com", dto.getEmail());
    }

    @Test
    @DisplayName("Should set and get all fields")
    void testSetAndGetAllFields() {
        UserProfileDTO dto = new UserProfileDTO();
        
        dto.setId(1L);
        dto.setUsername("testuser");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPhone("+1234567890");
        dto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        dto.setAnnualSalary(new BigDecimal("75000.00"));
        dto.setEmploymentStatus("Full-time");
        dto.setYearsWithEmployer(5);
        dto.setMaritalStatus("Single");
        dto.setRiskTolerance("Moderate");

        assertEquals(1L, dto.getId());
        assertEquals("testuser", dto.getUsername());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("john.doe@example.com", dto.getEmail());
        assertEquals("+1234567890", dto.getPhone());
        assertEquals(LocalDate.of(1990, 1, 1), dto.getDateOfBirth());
        assertEquals(new BigDecimal("75000.00"), dto.getAnnualSalary());
        assertEquals("Full-time", dto.getEmploymentStatus());
        assertEquals(5, dto.getYearsWithEmployer());
        assertEquals("Single", dto.getMaritalStatus());
        assertEquals("Moderate", dto.getRiskTolerance());
    }

    @Test
    @DisplayName("Should return correct display name")
    void testGetDisplayName() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");

        assertEquals("John Doe", dto.getDisplayName());
    }

    @Test
    @DisplayName("Should handle null values for optional fields")
    void testNullOptionalFields() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername("testuser");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        
        dto.setPhone(null);
        dto.setDateOfBirth(null);
        dto.setAnnualSalary(null);
        dto.setEmploymentStatus(null);
        dto.setYearsWithEmployer(null);
        dto.setMaritalStatus(null);
        dto.setRiskTolerance(null);

        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should handle BigDecimal precision for annual salary")
    void testBigDecimalPrecision() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername("testuser");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        
        BigDecimal salary = new BigDecimal("123456789012.99");
        dto.setAnnualSalary(salary);

        assertEquals(salary, dto.getAnnualSalary());
    }

    @Test
    @DisplayName("Should accept null email")
    void testNullEmail() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername("testuser");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail(null);

        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should handle phone number with various formats")
    void testPhoneNumberFormats() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername("testuser");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");

        // Test various phone formats
        String[] phoneFormats = {
            "+1234567890",
            "(123) 456-7890",
            "123-456-7890",
            "1234567890"
        };

        for (String phone : phoneFormats) {
            dto.setPhone(phone);
            assertEquals(phone, dto.getPhone());
        }
    }

    @Test
    @DisplayName("Should handle date of birth")
    void testDateOfBirth() {
        UserProfileDTO dto = new UserProfileDTO();
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        dto.setDateOfBirth(birthDate);

        assertEquals(birthDate, dto.getDateOfBirth());
    }

    @Test
    @DisplayName("Should handle various employment statuses")
    void testEmploymentStatuses() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername("testuser");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");

        String[] statuses = {"Full-time", "Part-time", "Self-employed", "Unemployed", "Retired"};

        for (String status : statuses) {
            dto.setEmploymentStatus(status);
            assertEquals(status, dto.getEmploymentStatus());
        }
    }

    @Test
    @DisplayName("Should handle various marital statuses")
    void testMaritalStatuses() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername("testuser");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");

        String[] statuses = {"Single", "Married", "Divorced", "Widowed"};

        for (String status : statuses) {
            dto.setMaritalStatus(status);
            assertEquals(status, dto.getMaritalStatus());
        }
    }

    @Test
    @DisplayName("Should handle various risk tolerance levels")
    void testRiskToleranceLevels() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername("testuser");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");

        String[] levels = {"Conservative", "Moderate", "Aggressive"};

        for (String level : levels) {
            dto.setRiskTolerance(level);
            assertEquals(level, dto.getRiskTolerance());
        }
    }

    @Test
    @DisplayName("Should handle zero years with employer")
    void testZeroYearsWithEmployer() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setYearsWithEmployer(0);

        assertEquals(0, dto.getYearsWithEmployer());
    }

    @Test
    @DisplayName("Should handle large years with employer")
    void testLargeYearsWithEmployer() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setYearsWithEmployer(50);

        assertEquals(50, dto.getYearsWithEmployer());
    }

    @Test
    @DisplayName("Should handle zero annual salary")
    void testZeroAnnualSalary() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setAnnualSalary(BigDecimal.ZERO);

        assertEquals(BigDecimal.ZERO, dto.getAnnualSalary());
    }

    @Test
    @DisplayName("Should handle negative ID")
    void testNegativeId() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(-1L);

        assertEquals(-1L, dto.getId());
    }

    @Test
    @DisplayName("Should handle large ID values")
    void testLargeIdValue() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(Long.MAX_VALUE);

        assertEquals(Long.MAX_VALUE, dto.getId());
    }

    @Test
    @DisplayName("Should handle special characters in names")
    void testSpecialCharactersInNames() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername("testuser");
        dto.setFirstName("Jean-Pierre");
        dto.setLastName("O'Brien");
        dto.setEmail("jean.pierre@example.com");

        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
        assertEquals("Jean-Pierre O'Brien", dto.getDisplayName());
    }

    @Test
    @DisplayName("Should handle email with subdomain")
    void testEmailWithSubdomain() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername("testuser");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@mail.example.com");

        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should handle future date of birth gracefully")
    void testFutureDateOfBirth() {
        UserProfileDTO dto = new UserProfileDTO();
        LocalDate futureDate = LocalDate.now().plusYears(1);
        dto.setDateOfBirth(futureDate);

        assertEquals(futureDate, dto.getDateOfBirth());
    }

    @Test
    @DisplayName("Should handle very old date of birth")
    void testVeryOldDateOfBirth() {
        UserProfileDTO dto = new UserProfileDTO();
        LocalDate oldDate = LocalDate.of(1900, 1, 1);
        dto.setDateOfBirth(oldDate);

        assertEquals(oldDate, dto.getDateOfBirth());
    }
}
