package com.finova.planning.service;

import com.finova.planning.dto.SocialSecurityDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Service for Social Security benefits calculations
 */
@Service
public class SocialSecurityService {
    
    /**
     * Calculate Social Security benefits
     */
    public SocialSecurityDTO calculateBenefits(SocialSecurityDTO ssDTO) {
        // Set defaults if not provided
        setDefaults(ssDTO);
        
        // Simplified calculation based on current salary
        BigDecimal baseMonthlyBenefit = calculateBaseBenefit(ssDTO.getCurrentSalary());
        
        // Calculate benefits at different ages
        ssDTO.setBenefitAt62(baseMonthlyBenefit.multiply(new BigDecimal("0.75"))); // 75% of full benefit
        ssDTO.setBenefitAt67(baseMonthlyBenefit); // Full benefit at age 67
        ssDTO.setBenefitAt70(baseMonthlyBenefit.multiply(new BigDecimal("1.32"))); // 132% of full benefit
        
        ssDTO.setFullRetirementAge(67);
        
        // Generate recommendations
        generateRecommendations(ssDTO);
        
        return ssDTO;
    }
    
    private void setDefaults(SocialSecurityDTO ssDTO) {
        if (ssDTO.getDateOfBirth() == null) {
            ssDTO.setDateOfBirth(LocalDate.of(1983, 5, 15));
        }
        if (ssDTO.getCurrentSalary() == null) {
            ssDTO.setCurrentSalary(new BigDecimal("78000"));
        }
        if (ssDTO.getYearsOfWorkHistory() == null) {
            ssDTO.setYearsOfWorkHistory(20);
        }
    }
    
    private BigDecimal calculateBaseBenefit(BigDecimal salary) {
        // Simplified formula: approximately 40% of pre-retirement income for average earners
        return salary.multiply(new BigDecimal("0.40")).divide(new BigDecimal("12"), BigDecimal.ROUND_HALF_UP);
    }
    
    private void generateRecommendations(SocialSecurityDTO ssDTO) {
        Integer currentAge = ssDTO.getCurrentAge();
        if (currentAge != null) {
            if (currentAge < 62) {
                ssDTO.setRecommendations("Continue working and building your earnings record. " +
                    "Consider delaying Social Security until full retirement age for maximum benefits.");
            } else if (currentAge < 67) {
                ssDTO.setRecommendations("You can claim reduced benefits now, but waiting until " +
                    "full retirement age (67) will give you 100% of your benefit.");
            } else {
                ssDTO.setRecommendations("You're at or past full retirement age. " +
                    "Delaying until age 70 can increase your benefits by up to 32%.");
            }
        }
    }
}
