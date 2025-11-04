package com.finova.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

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
    void shouldCreateUserProfileDTOWithDefaultConstructor() {
        UserProfileDTO dto = new UserProfileDTO();
        
        assertThat(dto).isNotNull();
    }

    @Test
    @DisplayName("Should create UserProfileDTO with parameterized constructor")
    void shouldCreateUserProfileDTOWithParameterizedConstructor() {
        UserProfileDTO dto = new UserProfileDTO("johndoe", "John", "Doe", "john@example.com");
        
        assertThat(dto.getUsername()).isEqualTo("johndoe");
        assertThat(dto.getFirstName()).isEqualTo("John");
        assertThat(dto.getLastName()).isEqualTo("Doe");
        assertThat(dto.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("Should validate UserProfileDTO with valid data")
    void shouldValidateUserProfileDTOWithValidData() {
        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(profileDTO);
        
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when username is blank")
    void shouldFailValidationWhenUsernameIsBlank() {
        profileDTO.setUsername("");
        
        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(profileDTO);
        
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getPropertyPath().toString().equals("username")
        );
    }

    @Test
    @DisplayName("Should fail validation when username is too short")
    void shouldFailValidationWhenUsernameIsTooShort() {
        profileDTO.setUsername("ab");
        
        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(profileDTO);
        
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail validation when username is too long")
    void shouldFailValidationWhenUsernameIsTooLong() {
        profileDTO.setUsername("a".repeat(51));
        
        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(profileDTO);
        
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail validation when first name is blank")
    void shouldFailValidationWhenFirstNameIsBlank() {
        profileDTO.setFirstName("");
        
        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(profileDTO);
        
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getPropertyPath().toString().equals("firstName")
        );
    }

    @Test
    @DisplayName("Should fail validation when last name is blank")
    void shouldFailValidationWhenLastNameIsBlank() {
        profileDTO.setLastName("");
        
        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(profileDTO);
        
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getPropertyPath().toString().equals("lastName")
        );
    }

    @Test
    @DisplayName("Should fail validation when email is invalid")
    void shouldFailValidationWhenEmailIsInvalid() {
        profileDTO.setEmail("invalid-email");
        
        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(profileDTO);
        
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getPropertyPath().toString().equals("email")
        );
    }

    @Test
    @DisplayName("Should set and get ID")
    void shouldSetAndGetId() {
        profileDTO.setId(123L);
        
        assertThat(profileDTO.getId()).isEqualTo(123L);
    }

    @Test
    @DisplayName("Should set and get phone number")
    void shouldSetAndGetPhoneNumber() {
        profileDTO.setPhone("555-1234");
        
        assertThat(profileDTO.getPhone()).isEqualTo("555-1234");
    }

    @Test
    @DisplayName("Should set and get date of birth")
    void shouldSetAndGetDateOfBirth() {
        LocalDate birthDate = LocalDate.of(1985, 6, 15);
        profileDTO.setDateOfBirth(birthDate);
        
        assertThat(profileDTO.getDateOfBirth()).isEqualTo(birthDate);
    }

    @Test
    @DisplayName("Should set and get annual salary")
    void shouldSetAndGetAnnualSalary() {
        BigDecimal salary = new BigDecimal("75000.00");
        profileDTO.setAnnualSalary(salary);
        
        assertThat(profileDTO.getAnnualSalary()).isEqualByComparingTo(salary);
    }

    @Test
    @DisplayName("Should set and get employment status")
    void shouldSetAndGetEmploymentStatus() {
        profileDTO.setEmploymentStatus("FULL_TIME");
        
        assertThat(profileDTO.getEmploymentStatus()).isEqualTo("FULL_TIME");
    }

    @Test
    @DisplayName("Should set and get years with employer")
    void shouldSetAndGetYearsWithEmployer() {
        profileDTO.setYearsWithEmployer(5);
        
        assertThat(profileDTO.getYearsWithEmployer()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should set and get marital status")
    void shouldSetAndGetMaritalStatus() {
        profileDTO.setMaritalStatus("MARRIED");
        
        assertThat(profileDTO.getMaritalStatus()).isEqualTo("MARRIED");
    }

    @Test
    @DisplayName("Should set and get risk tolerance")
    void shouldSetAndGetRiskTolerance() {
        profileDTO.setRiskTolerance("MODERATE");
        
        assertThat(profileDTO.getRiskTolerance()).isEqualTo("MODERATE");
    }

    @Test
    @DisplayName("Should return correct display name")
    void shouldReturnCorrectDisplayName() {
        profileDTO.setFirstName("Jane");
        profileDTO.setLastName("Smith");
        
        String displayName = profileDTO.getDisplayName();
        
        assertThat(displayName).isEqualTo("Jane Smith");
    }

    @Test
    @DisplayName("Should create complete user profile for retirement planning")
    void shouldCreateCompleteUserProfileForRetirementPlanning() {
        UserProfileDTO dto = new UserProfileDTO("jsmith", "Jane", "Smith", "jane.smith@finova.com");
        dto.setId(100L);
        dto.setPhone("555-9876");
        dto.setDateOfBirth(LocalDate.of(1980, 3, 20));
        dto.setAnnualSalary(new BigDecimal("95000.00"));
        dto.setEmploymentStatus("FULL_TIME");
        dto.setYearsWithEmployer(12);
        dto.setMaritalStatus("MARRIED");
        dto.setRiskTolerance("MODERATE");
        
        // Verify all fields
        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getUsername()).isEqualTo("jsmith");
        assertThat(dto.getFirstName()).isEqualTo("Jane");
        assertThat(dto.getLastName()).isEqualTo("Smith");
        assertThat(dto.getEmail()).isEqualTo("jane.smith@finova.com");
        assertThat(dto.getPhone()).isEqualTo("555-9876");
        assertThat(dto.getDateOfBirth()).isEqualTo(LocalDate.of(1980, 3, 20));
        assertThat(dto.getAnnualSalary()).isEqualByComparingTo(new BigDecimal("95000.00"));
        assertThat(dto.getEmploymentStatus()).isEqualTo("FULL_TIME");
        assertThat(dto.getYearsWithEmployer()).isEqualTo(12);
        assertThat(dto.getMaritalStatus()).isEqualTo("MARRIED");
        assertThat(dto.getRiskTolerance()).isEqualTo("MODERATE");
        assertThat(dto.getDisplayName()).isEqualTo("Jane Smith");
        
        // Validate
        Set<ConstraintViolation<UserProfileDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should handle high salary amounts for executive profiles")
    void shouldHandleHighSalaryAmountsForExecutiveProfiles() {
        BigDecimal executiveSalary = new BigDecimal("500000.00");
        profileDTO.setAnnualSalary(executiveSalary);
        
        assertThat(profileDTO.getAnnualSalary()).isEqualByComparingTo(executiveSalary);
    }

    @Test
    @DisplayName("Should handle various employment statuses")
    void shouldHandleVariousEmploymentStatuses() {
        String[] statuses = {"FULL_TIME", "PART_TIME", "CONTRACT", "SELF_EMPLOYED", "RETIRED"};
        
        for (String status : statuses) {
            profileDTO.setEmploymentStatus(status);
            assertThat(profileDTO.getEmploymentStatus()).isEqualTo(status);
        }
    }

    @Test
    @DisplayName("Should handle various risk tolerance levels")
    void shouldHandleVariousRiskToleranceLevels() {
        String[] riskLevels = {"CONSERVATIVE", "MODERATE", "AGGRESSIVE"};
        
        for (String level : riskLevels) {
            profileDTO.setRiskTolerance(level);
            assertThat(profileDTO.getRiskTolerance()).isEqualTo(level);
        }
    }
}
