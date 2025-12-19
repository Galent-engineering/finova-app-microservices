package com.finova.user.validation;

import com.finova.user.dto.UserProfileDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

/**
 * Validator for retirement age business rules
 */
public class RetirementAgeValidator implements ConstraintValidator<ValidRetirementAge, UserProfileDTO> {
    
    private static final int MIN_RETIREMENT_AGE = 55;
    private static final int MAX_RETIREMENT_AGE = 75;
    
    @Override
    public void initialize(ValidRetirementAge constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(UserProfileDTO userProfile, ConstraintValidatorContext context) {
        if (userProfile == null || userProfile.getDateOfBirth() == null) {
            return true; // Let other validators handle null checks
        }
        
        LocalDate dateOfBirth = userProfile.getDateOfBirth();
        LocalDate currentDate = LocalDate.now();
        
        // Calculate current age
        int currentAge = Period.between(dateOfBirth, currentDate).getYears();
        
        // Check if current age is reasonable (between 18 and 100)
        if (currentAge < 18 || currentAge > 100) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Current age must be between 18 and 100 years"
            ).addConstraintViolation();
            return false;
        }
        
        // For retirement planning, we assume a default retirement age of 65
        // if not specified, and validate that it's within reasonable bounds
        int retirementAge = 65; // Default retirement age
        
        // Validate retirement age range
        if (retirementAge < MIN_RETIREMENT_AGE || retirementAge > MAX_RETIREMENT_AGE) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                String.format("Retirement age must be between %d and %d years", 
                             MIN_RETIREMENT_AGE, MAX_RETIREMENT_AGE)
            ).addConstraintViolation();
            return false;
        }
        
        // Validate that retirement age is greater than current age
        if (retirementAge <= currentAge) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Retirement age must be greater than current age"
            ).addConstraintViolation();
            return false;
        }
        
        return true;
    }
}
