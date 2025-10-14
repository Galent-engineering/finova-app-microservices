package com.finova.account.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Retirement Account entity representing 401(k) and other retirement accounts
 * Based on the UI showing "New York City Deferred Compensation 401(a) Plan"
 */
@Entity
@Table(name = "retirement_accounts")
public class RetirementAccount {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId; // Reference to user in user-service
    
    @Column(name = "account_name", nullable = false)
    private String accountName;
    
    @Column(name = "account_type")
    private String accountType; // "401a", "401k", "IRA", etc.
    
    @Column(name = "current_balance", precision = 12, scale = 2)
    private BigDecimal currentBalance;
    
    @Column(name = "as_of_date")
    private LocalDate asOfDate;
    
    @Column(name = "estimated_monthly_income", precision = 10, scale = 2)
    private BigDecimal estimatedMonthlyIncome;
    
    @Column(name = "estimated_monthly_goal", precision = 10, scale = 2)
    private BigDecimal estimatedMonthlyGoal;
    
    @Column(name = "status")
    private String status; // "on_track", "behind", "ahead"
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // One-to-many relationship with contributions
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Contribution> contributions;
    
    // One-to-many relationship with income sources
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IncomeSource> incomeSources;
    
    // Constructors
    public RetirementAccount() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.currentBalance = BigDecimal.ZERO;
        this.status = "on_track";
        this.asOfDate = LocalDate.now();
    }
    
    public RetirementAccount(String accountName, String accountType, Long userId) {
        this();
        this.accountName = accountName;
        this.accountType = accountType;
        this.userId = userId;
    }
    
    // Update timestamp on save
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<Contribution> getContributions() { return contributions; }
    public void setContributions(List<Contribution> contributions) { this.contributions = contributions; }
    
    public List<IncomeSource> getIncomeSources() { return incomeSources; }
    public void setIncomeSources(List<IncomeSource> incomeSources) { this.incomeSources = incomeSources; }
    
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
     * Get formatted monthly income for display
     */
    public String getFormattedMonthlyIncome() {
        if (estimatedMonthlyIncome != null) {
            return String.format("$%,.0f", estimatedMonthlyIncome);
        }
        return "$0";
    }
    
    /**
     * Get formatted monthly goal for display
     */
    public String getFormattedMonthlyGoal() {
        if (estimatedMonthlyGoal != null) {
            return String.format("$%,.0f", estimatedMonthlyGoal);
        }
        return "$0";
    }
    
    /**
     * Check if user is on track with their retirement goals
     */
    public boolean isOnTrack() {
        return "on_track".equals(status);
    }
}
