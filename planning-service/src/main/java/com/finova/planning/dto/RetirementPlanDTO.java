package com.finova.planning.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * DTO for retirement planning calculations
 */
public class RetirementPlanDTO {
    
    private Long id;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @Positive(message = "Current age must be positive")
    private Integer currentAge;
    
    @Positive(message = "Retirement age must be positive")
    private Integer retirementAge;
    
    @Positive(message = "Expected retirement duration must be positive")
    private Integer expectedRetirementDuration;
    
    @PositiveOrZero(message = "Current savings must be positive or zero")
    private BigDecimal currentSavings;
    
    @PositiveOrZero(message = "Monthly contribution must be positive or zero")
    private BigDecimal monthlyContribution;
    
    @PositiveOrZero(message = "Employer match must be positive or zero")
    private BigDecimal employerMatch;
    
    @PositiveOrZero(message = "Desired monthly income must be positive or zero")
    private BigDecimal desiredMonthlyIncome;
    
    @PositiveOrZero(message = "Expected return rate must be positive or zero")
    private BigDecimal expectedReturnRate;
    
    @PositiveOrZero(message = "Expected inflation rate must be positive or zero")
    private BigDecimal expectedInflationRate;
    
    // Calculated results
    private Integer yearsToRetirement;
    private BigDecimal projectedBalance;
    private BigDecimal projectedMonthlyIncome;
    private String status; // "on_track", "behind", "ahead"
    private String recommendations;
    
    // Constructors
    public RetirementPlanDTO() {}
    
    public RetirementPlanDTO(Long userId, Integer currentAge, Integer retirementAge) {
        this.userId = userId;
        this.currentAge = currentAge;
        this.retirementAge = retirementAge;
        this.yearsToRetirement = retirementAge - currentAge;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Integer getCurrentAge() { return currentAge; }
    public void setCurrentAge(Integer currentAge) { 
        this.currentAge = currentAge; 
        calculateYearsToRetirement();
    }
    
    public Integer getRetirementAge() { return retirementAge; }
    public void setRetirementAge(Integer retirementAge) { 
        this.retirementAge = retirementAge; 
        calculateYearsToRetirement();
    }
    
    public Integer getExpectedRetirementDuration() { return expectedRetirementDuration; }
    public void setExpectedRetirementDuration(Integer expectedRetirementDuration) { 
        this.expectedRetirementDuration = expectedRetirementDuration; 
    }
    
    public BigDecimal getCurrentSavings() { return currentSavings; }
    public void setCurrentSavings(BigDecimal currentSavings) { this.currentSavings = currentSavings; }
    
    public BigDecimal getMonthlyContribution() { return monthlyContribution; }
    public void setMonthlyContribution(BigDecimal monthlyContribution) { this.monthlyContribution = monthlyContribution; }
    
    public BigDecimal getEmployerMatch() { return employerMatch; }
    public void setEmployerMatch(BigDecimal employerMatch) { this.employerMatch = employerMatch; }
    
    public BigDecimal getDesiredMonthlyIncome() { return desiredMonthlyIncome; }
    public void setDesiredMonthlyIncome(BigDecimal desiredMonthlyIncome) { this.desiredMonthlyIncome = desiredMonthlyIncome; }
    
    public BigDecimal getExpectedReturnRate() { return expectedReturnRate; }
    public void setExpectedReturnRate(BigDecimal expectedReturnRate) { this.expectedReturnRate = expectedReturnRate; }
    
    public BigDecimal getExpectedInflationRate() { return expectedInflationRate; }
    public void setExpectedInflationRate(BigDecimal expectedInflationRate) { this.expectedInflationRate = expectedInflationRate; }
    
    public Integer getYearsToRetirement() { return yearsToRetirement; }
    public void setYearsToRetirement(Integer yearsToRetirement) { this.yearsToRetirement = yearsToRetirement; }
    
    public BigDecimal getProjectedBalance() { return projectedBalance; }
    public void setProjectedBalance(BigDecimal projectedBalance) { this.projectedBalance = projectedBalance; }
    
    public BigDecimal getProjectedMonthlyIncome() { return projectedMonthlyIncome; }
    public void setProjectedMonthlyIncome(BigDecimal projectedMonthlyIncome) { this.projectedMonthlyIncome = projectedMonthlyIncome; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    
    private void calculateYearsToRetirement() {
        if (currentAge != null && retirementAge != null) {
            this.yearsToRetirement = retirementAge - currentAge;
        }
    }
    
    /**
     * Get formatted projected balance for display
     */
    public String getFormattedProjectedBalance() {
        if (projectedBalance != null) {
            return String.format("$%,.0f", projectedBalance);
        }
        return "$0";
    }
    
    /**
     * Get formatted projected monthly income for display
     */
    public String getFormattedProjectedMonthlyIncome() {
        if (projectedMonthlyIncome != null) {
            return String.format("$%,.0f", projectedMonthlyIncome);
        }
        return "$0";
    }
    
    /**
     * Check if the user is on track for retirement
     */
    public boolean isOnTrack() {
        return "on_track".equals(status) || "ahead".equals(status);
    }
}
