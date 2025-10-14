package com.finova.planning.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for investment strategy and portfolio allocation
 */
public class InvestmentStrategyDTO {
    
    private Long id;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @PositiveOrZero(message = "Portfolio value must be positive or zero")
    private BigDecimal portfolioValue;
    
    private String currentStrategy; // "Conservative", "Moderate Growth", "Aggressive Growth"
    private String riskLevel; // "Low", "Moderate", "High"
    
    // Asset allocation percentages
    @PositiveOrZero(message = "Stocks percentage must be positive or zero")
    private Integer stocksPercentage;
    
    @PositiveOrZero(message = "Bonds percentage must be positive or zero")
    private Integer bondsPercentage;
    
    @PositiveOrZero(message = "Cash percentage must be positive or zero")
    private Integer cashPercentage;
    
    // Asset allocation amounts
    private BigDecimal stocksAmount;
    private BigDecimal bondsAmount;
    private BigDecimal cashAmount;
    
    // Performance data
    private String returnPercentage;
    private BigDecimal contributions;
    private BigDecimal totalGain;
    
    // Recommendations
    private String recommendations;
    private List<String> suggestedAllocations;
    
    // Constructors
    public InvestmentStrategyDTO() {}
    
    public InvestmentStrategyDTO(Long userId, BigDecimal portfolioValue, String currentStrategy, String riskLevel) {
        this.userId = userId;
        this.portfolioValue = portfolioValue;
        this.currentStrategy = currentStrategy;
        this.riskLevel = riskLevel;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public BigDecimal getPortfolioValue() { return portfolioValue; }
    public void setPortfolioValue(BigDecimal portfolioValue) { 
        this.portfolioValue = portfolioValue;
        calculateAssetAmounts();
    }
    
    public String getCurrentStrategy() { return currentStrategy; }
    public void setCurrentStrategy(String currentStrategy) { this.currentStrategy = currentStrategy; }
    
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    
    public Integer getStocksPercentage() { return stocksPercentage; }
    public void setStocksPercentage(Integer stocksPercentage) { 
        this.stocksPercentage = stocksPercentage;
        calculateAssetAmounts();
    }
    
    public Integer getBondsPercentage() { return bondsPercentage; }
    public void setBondsPercentage(Integer bondsPercentage) { 
        this.bondsPercentage = bondsPercentage;
        calculateAssetAmounts();
    }
    
    public Integer getCashPercentage() { return cashPercentage; }
    public void setCashPercentage(Integer cashPercentage) { 
        this.cashPercentage = cashPercentage;
        calculateAssetAmounts();
    }
    
    public BigDecimal getStocksAmount() { return stocksAmount; }
    public void setStocksAmount(BigDecimal stocksAmount) { this.stocksAmount = stocksAmount; }
    
    public BigDecimal getBondsAmount() { return bondsAmount; }
    public void setBondsAmount(BigDecimal bondsAmount) { this.bondsAmount = bondsAmount; }
    
    public BigDecimal getCashAmount() { return cashAmount; }
    public void setCashAmount(BigDecimal cashAmount) { this.cashAmount = cashAmount; }
    
    public String getReturnPercentage() { return returnPercentage; }
    public void setReturnPercentage(String returnPercentage) { this.returnPercentage = returnPercentage; }
    
    public BigDecimal getContributions() { return contributions; }
    public void setContributions(BigDecimal contributions) { this.contributions = contributions; }
    
    public BigDecimal getTotalGain() { return totalGain; }
    public void setTotalGain(BigDecimal totalGain) { this.totalGain = totalGain; }
    
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    
    public List<String> getSuggestedAllocations() { return suggestedAllocations; }
    public void setSuggestedAllocations(List<String> suggestedAllocations) { this.suggestedAllocations = suggestedAllocations; }
    
    /**
     * Calculate asset amounts based on percentages and portfolio value
     */
    private void calculateAssetAmounts() {
        if (portfolioValue != null) {
            if (stocksPercentage != null) {
                stocksAmount = portfolioValue.multiply(new BigDecimal(stocksPercentage)).divide(new BigDecimal(100));
            }
            if (bondsPercentage != null) {
                bondsAmount = portfolioValue.multiply(new BigDecimal(bondsPercentage)).divide(new BigDecimal(100));
            }
            if (cashPercentage != null) {
                cashAmount = portfolioValue.multiply(new BigDecimal(cashPercentage)).divide(new BigDecimal(100));
            }
        }
    }
    
    /**
     * Get formatted portfolio value
     */
    public String getFormattedPortfolioValue() {
        return portfolioValue != null ? String.format("$%,.0f", portfolioValue) : "$0";
    }
    
    /**
     * Get formatted asset amounts
     */
    public String getFormattedStocksAmount() {
        return stocksAmount != null ? String.format("$%,.0f", stocksAmount) : "$0";
    }
    
    public String getFormattedBondsAmount() {
        return bondsAmount != null ? String.format("$%,.0f", bondsAmount) : "$0";
    }
    
    public String getFormattedCashAmount() {
        return cashAmount != null ? String.format("$%,.0f", cashAmount) : "$0";
    }
    
    /**
     * Get formatted total gain
     */
    public String getFormattedTotalGain() {
        return totalGain != null ? String.format("$%,.0f", totalGain) : "$0";
    }
    
    /**
     * Validate allocation percentages sum to 100
     */
    public boolean isValidAllocation() {
        if (stocksPercentage != null && bondsPercentage != null && cashPercentage != null) {
            return (stocksPercentage + bondsPercentage + cashPercentage) == 100;
        }
        return false;
    }
}
