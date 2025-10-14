package com.finova.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for RetirementAccount data transfer
 */
public class RetirementAccountDTO {
    
    private Long id;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotBlank(message = "Account name is required")
    private String accountName;
    
    private String accountType;
    
    @PositiveOrZero(message = "Current balance must be positive or zero")
    private BigDecimal currentBalance;
    
    private LocalDate asOfDate;
    
    @PositiveOrZero(message = "Estimated monthly income must be positive or zero")
    private BigDecimal estimatedMonthlyIncome;
    
    @PositiveOrZero(message = "Estimated monthly goal must be positive or zero")
    private BigDecimal estimatedMonthlyGoal;
    
    private String status;
    
    private List<ContributionDTO> contributions;
    private List<IncomeSourceDTO> incomeSources;
    
    // Constructors
    public RetirementAccountDTO() {}
    
    public RetirementAccountDTO(Long id, Long userId, String accountName, String accountType, 
                               BigDecimal currentBalance, BigDecimal estimatedMonthlyIncome, 
                               BigDecimal estimatedMonthlyGoal, String status) {
        this.id = id;
        this.userId = userId;
        this.accountName = accountName;
        this.accountType = accountType;
        this.currentBalance = currentBalance;
        this.estimatedMonthlyIncome = estimatedMonthlyIncome;
        this.estimatedMonthlyGoal = estimatedMonthlyGoal;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    
    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }
    
    public LocalDate getAsOfDate() { return asOfDate; }
    public void setAsOfDate(LocalDate asOfDate) { this.asOfDate = asOfDate; }
    
    public BigDecimal getEstimatedMonthlyIncome() { return estimatedMonthlyIncome; }
    public void setEstimatedMonthlyIncome(BigDecimal estimatedMonthlyIncome) { 
        this.estimatedMonthlyIncome = estimatedMonthlyIncome; 
    }
    
    public BigDecimal getEstimatedMonthlyGoal() { return estimatedMonthlyGoal; }
    public void setEstimatedMonthlyGoal(BigDecimal estimatedMonthlyGoal) { 
        this.estimatedMonthlyGoal = estimatedMonthlyGoal; 
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public List<ContributionDTO> getContributions() { return contributions; }
    public void setContributions(List<ContributionDTO> contributions) { this.contributions = contributions; }
    
    public List<IncomeSourceDTO> getIncomeSources() { return incomeSources; }
    public void setIncomeSources(List<IncomeSourceDTO> incomeSources) { this.incomeSources = incomeSources; }
    
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
     * Check if user is on track with their retirement goals
     */
    public boolean isOnTrack() {
        return "on_track".equals(status);
    }
}
