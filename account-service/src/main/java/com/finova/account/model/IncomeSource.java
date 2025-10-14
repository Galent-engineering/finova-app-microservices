package com.finova.account.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Income Source entity representing various retirement income sources
 * Based on the UI showing 401k, Traditional IRA, and Pension income sources
 */
@Entity
@Table(name = "income_sources")
public class IncomeSource {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "source_type", nullable = false)
    private String sourceType; // "401k", "ira", "pension", "social_security"
    
    @Column(name = "source_name")
    private String sourceName; // e.g., "Traditional IRA - Fidelity"
    
    @Column(name = "current_balance", precision = 12, scale = 2)
    private BigDecimal currentBalance;
    
    @Column(name = "projected_monthly_income", precision = 10, scale = 2)
    private BigDecimal projectedMonthlyIncome;
    
    @Column(name = "provider")
    private String provider; // e.g., "Fidelity", "Finova"
    
    @Column(name = "is_active")
    private boolean active = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Many-to-one relationship with retirement account
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private RetirementAccount account;
    
    // Constructors
    public IncomeSource() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.active = true;
    }
    
    public IncomeSource(String sourceType, String sourceName, BigDecimal currentBalance, 
                       BigDecimal projectedMonthlyIncome) {
        this();
        this.sourceType = sourceType;
        this.sourceName = sourceName;
        this.currentBalance = currentBalance;
        this.projectedMonthlyIncome = projectedMonthlyIncome;
    }
    
    // Update timestamp on save
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
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
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public RetirementAccount getAccount() { return account; }
    public void setAccount(RetirementAccount account) { this.account = account; }
    
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
     * Get display name including provider if available
     */
    public String getFullDisplayName() {
        if (provider != null && !provider.isEmpty()) {
            return sourceName + " - " + provider;
        }
        return sourceName != null ? sourceName : getDisplayType();
    }
}
