package com.finova.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * DTO for IncomeSource data transfer
 */
public class IncomeSourceDTO {
    
    private Long id;
    private Long accountId;
    
    @NotBlank(message = "Source type is required")
    private String sourceType;
    
    private String sourceName;
    
    @PositiveOrZero(message = "Current balance must be positive or zero")
    private BigDecimal currentBalance;
    
    @PositiveOrZero(message = "Projected monthly income must be positive or zero")
    private BigDecimal projectedMonthlyIncome;
    
    private String provider;
    private boolean active = true;
    
    // Constructors
    public IncomeSourceDTO() {}
    
    public IncomeSourceDTO(Long id, String sourceType, String sourceName, 
                          BigDecimal currentBalance, BigDecimal projectedMonthlyIncome, 
                          String provider) {
        this.id = id;
        this.sourceType = sourceType;
        this.sourceName = sourceName;
        this.currentBalance = currentBalance;
        this.projectedMonthlyIncome = projectedMonthlyIncome;
        this.provider = provider;
        this.active = true;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    
    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }
    
    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }
    
    public BigDecimal getProjectedMonthlyIncome() { return projectedMonthlyIncome; }
    public void setProjectedMonthlyIncome(BigDecimal projectedMonthlyIncome) { 
        this.projectedMonthlyIncome = projectedMonthlyIncome; 
    }
    
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    /**
     * Get display name for source type
     */
    public String getDisplayType() {
        switch (sourceType) {
            case "401k":
                return "401(k) Withdrawals";
            case "ira":
                return "IRA Withdrawals";
            case "pension":
                return "Pension";
            case "social_security":
                return "Social Security";
            default:
                return sourceType;
        }
    }
    
    /**
     * Get formatted current balance for display
     */
    public String getFormattedBalance() {
        if (currentBalance != null) {
            return String.format("$%,.2f", currentBalance);
        }
        return "$0.00";
    }
    
    /**
     * Get formatted projected monthly income for display
     */
    public String getFormattedMonthlyIncome() {
        if (projectedMonthlyIncome != null) {
            return String.format("$%,.0f", projectedMonthlyIncome);
        }
        return "$0";
    }
}
