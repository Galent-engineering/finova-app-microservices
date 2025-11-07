package com.finova.account.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO for IncomeSource data transfer
 */
public class IncomeSourceDTO {
    
    private Long id;
    
    @NotNull(message = "Account ID is required")
    @Positive(message = "Account ID must be positive")
    private Long accountId;
    
    @NotBlank(message = "Source type is required")
    @Pattern(regexp = "^(401k|ira|pension|social_security|annuity|other)$", 
             message = "Source type must be one of: 401k, ira, pension, social_security, annuity, other")
    private String sourceType;
    
    @Size(max = 100, message = "Source name must not exceed 100 characters")
    private String sourceName;
    
    @DecimalMin(value = "0.0", message = "Current balance must be zero or positive")
    @DecimalMax(value = "10000000.0", message = "Current balance cannot exceed $10,000,000")
    private BigDecimal currentBalance;
    
    @DecimalMin(value = "0.0", message = "Projected monthly income must be zero or positive")
    @DecimalMax(value = "50000.0", message = "Projected monthly income cannot exceed $50,000")
    private BigDecimal projectedMonthlyIncome;
    
    @Size(max = 100, message = "Provider name must not exceed 100 characters")
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
