package com.finova.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for Contribution data transfer
 */
public class ContributionDTO {
    
    private Long id;
    private Long accountId;
    
    @NotBlank(message = "Contribution type is required")
    private String contributionType;
    
    @PositiveOrZero(message = "Percentage must be positive or zero")
    private BigDecimal percentage;
    
    @PositiveOrZero(message = "Monthly amount must be positive or zero")
    private BigDecimal monthlyAmount;
    
    private BigDecimal annualAmount;
    private LocalDate effectiveDate;
    
    // Constructors
    public ContributionDTO() {}
    
    public ContributionDTO(Long id, String contributionType, BigDecimal percentage, 
                          BigDecimal monthlyAmount, BigDecimal annualAmount) {
        this.id = id;
        this.contributionType = contributionType;
        this.percentage = percentage;
        this.monthlyAmount = monthlyAmount;
        this.annualAmount = annualAmount;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    
    public String getContributionType() { return contributionType; }
    public void setContributionType(String contributionType) { this.contributionType = contributionType; }
    
    public BigDecimal getPercentage() { return percentage; }
    public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
    
    public BigDecimal getMonthlyAmount() { return monthlyAmount; }
    public void setMonthlyAmount(BigDecimal monthlyAmount) { this.monthlyAmount = monthlyAmount; }
    
    public BigDecimal getAnnualAmount() { return annualAmount; }
    public void setAnnualAmount(BigDecimal annualAmount) { this.annualAmount = annualAmount; }
    
    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }
    
    /**
     * Get display name for contribution type
     */
    public String getDisplayType() {
        switch (contributionType) {
            case "pre_tax":
                return "Pre-Tax Contribution";
            case "roth":
                return "Roth Contribution";
            case "employer_match":
                return "Employer Match";
            default:
                return contributionType;
        }
    }
    
    /**
     * Get formatted monthly amount for display
     */
    public String getFormattedMonthlyAmount() {
        if (monthlyAmount != null) {
            return String.format("$%,.0f", monthlyAmount);
        }
        return "$0";
    }
}
