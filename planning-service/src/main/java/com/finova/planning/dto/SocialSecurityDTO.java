package com.finova.planning.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for Social Security benefits estimation
 */
public class SocialSecurityDTO {
    
    private Long id;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    private LocalDate dateOfBirth;
    
    @PositiveOrZero(message = "Current salary must be positive or zero")
    private BigDecimal currentSalary;
    
    @PositiveOrZero(message = "Years of work history must be positive or zero")
    private Integer yearsOfWorkHistory;
    
    private String claimAge; // "Age 62", "Age 67 (Full Retirement Age)", "Age 70"
    
    // Calculated benefits
    private BigDecimal benefitAt62;
    private BigDecimal benefitAt67;
    private BigDecimal benefitAt70;
    
    // Additional information
    private Integer fullRetirementAge;
    private String recommendations;
    
    // Constructors
    public SocialSecurityDTO() {}
    
    public SocialSecurityDTO(Long userId, LocalDate dateOfBirth, BigDecimal currentSalary, Integer yearsOfWorkHistory) {
        this.userId = userId;
        this.dateOfBirth = dateOfBirth;
        this.currentSalary = currentSalary;
        this.yearsOfWorkHistory = yearsOfWorkHistory;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public BigDecimal getCurrentSalary() { return currentSalary; }
    public void setCurrentSalary(BigDecimal currentSalary) { this.currentSalary = currentSalary; }
    
    public Integer getYearsOfWorkHistory() { return yearsOfWorkHistory; }
    public void setYearsOfWorkHistory(Integer yearsOfWorkHistory) { this.yearsOfWorkHistory = yearsOfWorkHistory; }
    
    public String getClaimAge() { return claimAge; }
    public void setClaimAge(String claimAge) { this.claimAge = claimAge; }
    
    public BigDecimal getBenefitAt62() { return benefitAt62; }
    public void setBenefitAt62(BigDecimal benefitAt62) { this.benefitAt62 = benefitAt62; }
    
    public BigDecimal getBenefitAt67() { return benefitAt67; }
    public void setBenefitAt67(BigDecimal benefitAt67) { this.benefitAt67 = benefitAt67; }
    
    public BigDecimal getBenefitAt70() { return benefitAt70; }
    public void setBenefitAt70(BigDecimal benefitAt70) { this.benefitAt70 = benefitAt70; }
    
    public Integer getFullRetirementAge() { return fullRetirementAge; }
    public void setFullRetirementAge(Integer fullRetirementAge) { this.fullRetirementAge = fullRetirementAge; }
    
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    
    /**
     * Get current age from date of birth
     */
    public Integer getCurrentAge() {
        if (dateOfBirth != null) {
            return LocalDate.now().getYear() - dateOfBirth.getYear();
        }
        return null;
    }
    
    /**
     * Get formatted date of birth
     */
    public String getFormattedDateOfBirth() {
        if (dateOfBirth != null) {
            return String.format("%02d-%02d-%d", 
                dateOfBirth.getDayOfMonth(), 
                dateOfBirth.getMonthValue(), 
                dateOfBirth.getYear());
        }
        return null;
    }
    
    /**
     * Get formatted benefit amounts
     */
    public String getFormattedBenefitAt62() {
        return benefitAt62 != null ? String.format("$%,.0f", benefitAt62) : "$0";
    }
    
    public String getFormattedBenefitAt67() {
        return benefitAt67 != null ? String.format("$%,.0f", benefitAt67) : "$0";
    }
    
    public String getFormattedBenefitAt70() {
        return benefitAt70 != null ? String.format("$%,.0f", benefitAt70) : "$0";
    }
}
