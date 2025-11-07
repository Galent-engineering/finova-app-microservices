package com.finova.planning.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO for retirement planning calculations
 */
public class RetirementPlanDTO {
    
    private Long id;
    
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;
    
    @Positive(message = "Current age must be positive")
    @Min(value = 18, message = "Current age must be at least 18")
    @Max(value = 100, message = "Current age cannot exceed 100")
    private Integer currentAge;
    
    @Positive(message = "Retirement age must be positive")
    @Min(value = 55, message = "Retirement age must be at least 55")
    @Max(value = 75, message = "Retirement age cannot exceed 75")
    private Integer retirementAge;
    
    @Positive(message = "Expected retirement duration must be positive")
    @Min(value = 1, message = "Expected retirement duration must be at least 1 year")
    @Max(value = 40, message = "Expected retirement duration cannot exceed 40 years")
    private Integer expectedRetirementDuration;
    
    @DecimalMin(value = "0.0", message = "Current savings must be zero or positive")
    @DecimalMax(value = "10000000.0", message = "Current savings cannot exceed $10,000,000")
    private BigDecimal currentSavings;
    
    @DecimalMin(value = "0.0", message = "Monthly contribution must be zero or positive")
    @DecimalMax(value = "50000.0", message = "Monthly contribution cannot exceed $50,000")
    private BigDecimal monthlyContribution;
    
    @DecimalMin(value = "0.0", message = "Employer match must be zero or positive")
    @DecimalMax(value = "100.0", message = "Employer match cannot exceed 100%")
    private BigDecimal employerMatch;
    
    @DecimalMin(value = "0.0", message = "Desired monthly income must be zero or positive")
    @DecimalMax(value = "100000.0", message = "Desired monthly income cannot exceed $100,000")
    private BigDecimal desiredMonthlyIncome;
    
    @DecimalMin(value = "0.0", message = "Expected return rate must be zero or positive")
    @DecimalMax(value = "50.0", message = "Expected return rate cannot exceed 50%")
    private BigDecimal expectedReturnRate;
    
    @DecimalMin(value = "0.0", message = "Expected inflation rate must be zero or positive")
    @DecimalMax(value = "20.0", message = "Expected inflation rate cannot exceed 20%")
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
