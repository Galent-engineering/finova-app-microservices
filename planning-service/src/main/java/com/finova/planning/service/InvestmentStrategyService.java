package com.finova.planning.service;

import com.finova.planning.dto.InvestmentStrategyDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Service for investment strategy recommendations
 */
@Service
public class InvestmentStrategyService {
    
    /**
     * Generate investment strategy recommendations
     */
    public InvestmentStrategyDTO generateStrategy(Long userId, Integer age) {
        InvestmentStrategyDTO strategy = new InvestmentStrategyDTO();
        strategy.setUserId(userId);
        strategy.setPortfolioValue(new BigDecimal("106965"));
        
        // Age-based allocation strategy
        if (age == null) age = 42; // Default age
        
        if (age < 35) {
            // Aggressive Growth
            strategy.setCurrentStrategy("Aggressive Growth");
            strategy.setRiskLevel("High");
            strategy.setStocksPercentage(80);
            strategy.setBondsPercentage(15);
            strategy.setCashPercentage(5);
        } else if (age < 55) {
            // Moderate Growth
            strategy.setCurrentStrategy("Moderate Growth");
            strategy.setRiskLevel("Moderate");
            strategy.setStocksPercentage(65);
            strategy.setBondsPercentage(30);
            strategy.setCashPercentage(5);
        } else {
            // Conservative
            strategy.setCurrentStrategy("Conservative");
            strategy.setRiskLevel("Low");
            strategy.setStocksPercentage(40);
            strategy.setBondsPercentage(50);
            strategy.setCashPercentage(10);
        }
        
        // Set performance data (sample)
        strategy.setReturnPercentage("+8.4%");
        strategy.setContributions(new BigDecimal("7800"));
        strategy.setTotalGain(new BigDecimal("8973"));
        
        // Generate recommendations
        generateRecommendations(strategy, age);
        
        return strategy;
    }
    
    private void generateRecommendations(InvestmentStrategyDTO strategy, Integer age) {
        StringBuilder recommendations = new StringBuilder();
        
        if (age < 35) {
            recommendations.append("At your age, you can afford to take more risk for potentially higher returns. ");
            recommendations.append("Consider maintaining a higher stock allocation.");
        } else if (age < 55) {
            recommendations.append("Your moderate allocation balances growth potential with risk management. ");
            recommendations.append("Consider gradually reducing stock allocation as you approach retirement.");
        } else {
            recommendations.append("As you near retirement, focus on capital preservation. ");
            recommendations.append("Consider increasing bond allocation for stability.");
        }
        
        strategy.setRecommendations(recommendations.toString());
        strategy.setSuggestedAllocations(Arrays.asList(
            "Diversify across asset classes",
            "Rebalance portfolio quarterly",
            "Consider low-cost index funds",
            "Review allocation annually"
        ));
    }
}
