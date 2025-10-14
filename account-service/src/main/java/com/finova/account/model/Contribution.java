package com.finova.account.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Contribution entity representing retirement contributions
 * Based on the UI showing Pre-Tax and Roth contributions with percentages and dollar amounts
 */
@Entity
@Table(name = "contributions")
public class Contribution {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "contribution_type", nullable = false)
    private String contributionType; // "pre_tax", "roth", "employer_match"
    
    @Column(name = "percentage", precision = 5, scale = 2)
    private BigDecimal percentage;
    
    @Column(name = "monthly_amount", precision = 10, scale = 2)
    private BigDecimal monthlyAmount;
    
    @Column(name = "annual_amount", precision = 12, scale = 2)
    private BigDecimal annualAmount;
    
    @Column(name = "effective_date")
    private LocalDate effectiveDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Many-to-one relationship with retirement account
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private RetirementAccount account;
    
    // Constructors
    public Contribution() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Contribution(String contributionType, BigDecimal percentage, BigDecimal monthlyAmount, 
                       RetirementAccount account) {
        this();
        this.contributionType = contributionType;
        this.percentage = percentage;
        this.monthlyAmount = monthlyAmount;
        this.account = account;
        
        // Calculate annual amount from monthly
        if (monthlyAmount != null) {
            this.annualAmount = monthlyAmount.multiply(new BigDecimal("12"));
        }
    }
    
    // Update timestamp on save
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getContributionType() { return contributionType; }
    public void setContributionType(String contributionType) { this.contributionType = contributionType; }
    
    public BigDecimal getPercentage() { return percentage; }
    public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
    
    public BigDecimal getMonthlyAmount() { return monthlyAmount; }
    public void setMonthlyAmount(BigDecimal monthlyAmount) { 
        this.monthlyAmount = monthlyAmount;
        // Auto-calculate annual amount
        if (monthlyAmount != null) {
            this.annualAmount = monthlyAmount.multiply(new BigDecimal("12"));
        }
    }
    
    public BigDecimal getAnnualAmount() { return annualAmount; }
    public void setAnnualAmount(BigDecimal annualAmount) { this.annualAmount = annualAmount; }
    
    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public RetirementAccount getAccount() { return account; }
    public void setAccount(RetirementAccount account) { this.account = account; }
    
    /**
     * Get formatted monthly amount for display
     */
    public String getFormattedMonthlyAmount() {
        if (monthlyAmount != null) {
            return String.format("$%,.0f", monthlyAmount);
        }
        return "$0";
    }
    
    /**
     * Get formatted annual amount for display
     */
    public String getFormattedAnnualAmount() {
        if (annualAmount != null) {
            return String.format("$%,.0f", annualAmount);
        }
        return "$0";
    }
    
    /**
     * Get formatted percentage for display
     */
    public String getFormattedPercentage() {
        if (percentage != null) {
            return percentage.toString() + "%";
        }
        return "0%";
    }
    
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
}
