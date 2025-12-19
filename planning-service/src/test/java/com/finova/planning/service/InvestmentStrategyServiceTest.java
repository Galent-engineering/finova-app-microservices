package com.finova.planning.service;

import com.finova.planning.dto.InvestmentStrategyDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InvestmentStrategyService
 */
@SpringBootTest
class InvestmentStrategyServiceTest {

    @Autowired
    private InvestmentStrategyService investmentStrategyService;

    @Test
    void generateStrategy_WithYoungPerson_ShouldReturnAggressiveStrategy() {
        // Given
        Long userId = 1L;
        Integer age = 25;

        // When
        InvestmentStrategyDTO result = investmentStrategyService.generateStrategy(userId, age);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("Aggressive Growth", result.getCurrentStrategy());
        assertEquals("High", result.getRiskLevel());
        assertEquals(80, result.getStocksPercentage());
        assertEquals(15, result.getBondsPercentage());
        assertEquals(5, result.getCashPercentage());
        assertNotNull(result.getRecommendations());
        assertTrue(result.getRecommendations().contains("higher risk"));
        assertTrue(result.getRecommendations().contains("higher stock allocation"));
    }

    @Test
    void generateStrategy_WithMiddleAgedPerson_ShouldReturnModerateStrategy() {
        // Given
        Long userId = 2L;
        Integer age = 45;

        // When
        InvestmentStrategyDTO result = investmentStrategyService.generateStrategy(userId, age);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("Moderate Growth", result.getCurrentStrategy());
        assertEquals("Moderate", result.getRiskLevel());
        assertEquals(65, result.getStocksPercentage());
        assertEquals(30, result.getBondsPercentage());
        assertEquals(5, result.getCashPercentage());
        assertNotNull(result.getRecommendations());
        assertTrue(result.getRecommendations().contains("moderate allocation"));
        assertTrue(result.getRecommendations().contains("gradually reducing"));
    }

    @Test
    void generateStrategy_WithOlderPerson_ShouldReturnConservativeStrategy() {
        // Given
        Long userId = 3L;
        Integer age = 60;

        // When
        InvestmentStrategyDTO result = investmentStrategyService.generateStrategy(userId, age);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("Conservative", result.getCurrentStrategy());
        assertEquals("Low", result.getRiskLevel());
        assertEquals(40, result.getStocksPercentage());
        assertEquals(50, result.getBondsPercentage());
        assertEquals(10, result.getCashPercentage());
        assertNotNull(result.getRecommendations());
        assertTrue(result.getRecommendations().contains("capital preservation"));
        assertTrue(result.getRecommendations().contains("increasing bond allocation"));
    }

    @Test
    void generateStrategy_WithNullAge_ShouldUseDefaultAge() {
        // Given
        Long userId = 4L;
        Integer age = null;

        // When
        InvestmentStrategyDTO result = investmentStrategyService.generateStrategy(userId, age);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        // Should use default age of 42, which falls in moderate category
        assertEquals("Moderate Growth", result.getCurrentStrategy());
        assertEquals("Moderate", result.getRiskLevel());
        assertEquals(65, result.getStocksPercentage());
    }

    @Test
    void generateStrategy_WithAge35_ShouldReturnModerateStrategy() {
        // Given
        Long userId = 5L;
        Integer age = 35;

        // When
        InvestmentStrategyDTO result = investmentStrategyService.generateStrategy(userId, age);

        // Then
        assertNotNull(result);
        assertEquals("Moderate Growth", result.getCurrentStrategy());
        assertEquals("Moderate", result.getRiskLevel());
        assertEquals(65, result.getStocksPercentage());
        assertEquals(30, result.getBondsPercentage());
        assertEquals(5, result.getCashPercentage());
    }

    @Test
    void generateStrategy_WithAge55_ShouldReturnConservativeStrategy() {
        // Given
        Long userId = 6L;
        Integer age = 55;

        // When
        InvestmentStrategyDTO result = investmentStrategyService.generateStrategy(userId, age);

        // Then
        assertNotNull(result);
        assertEquals("Conservative", result.getCurrentStrategy());
        assertEquals("Low", result.getRiskLevel());
        assertEquals(40, result.getStocksPercentage());
        assertEquals(50, result.getBondsPercentage());
        assertEquals(10, result.getCashPercentage());
    }

    @Test
    void generateStrategy_PerformanceData_ShouldBeSet() {
        // Given
        Long userId = 7L;
        Integer age = 30;

        // When
        InvestmentStrategyDTO result = investmentStrategyService.generateStrategy(userId, age);

        // Then
        assertNotNull(result.getReturnPercentage());
        assertNotNull(result.getContributions());
        assertNotNull(result.getTotalGain());
        assertEquals("+8.4%", result.getReturnPercentage());
        assertEquals(new BigDecimal("7800"), result.getContributions());
        assertEquals(new BigDecimal("8973"), result.getTotalGain());
    }

    @Test
    void generateStrategy_PortfolioValue_ShouldBeSet() {
        // Given
        Long userId = 8L;
        Integer age = 40;

        // When
        InvestmentStrategyDTO result = investmentStrategyService.generateStrategy(userId, age);

        // Then
        assertNotNull(result.getPortfolioValue());
        assertEquals(new BigDecimal("106965"), result.getPortfolioValue());
    }

    @Test
    void generateStrategy_SuggestedAllocations_ShouldBeSet() {
        // Given
        Long userId = 9L;
        Integer age = 50;

        // When
        InvestmentStrategyDTO result = investmentStrategyService.generateStrategy(userId, age);

        // Then
        assertNotNull(result.getSuggestedAllocations());
        assertEquals(4, result.getSuggestedAllocations().size());
        assertTrue(result.getSuggestedAllocations().contains("Diversify across asset classes"));
        assertTrue(result.getSuggestedAllocations().contains("Rebalance portfolio quarterly"));
        assertTrue(result.getSuggestedAllocations().contains("Consider low-cost index funds"));
        assertTrue(result.getSuggestedAllocations().contains("Review allocation annually"));
    }

    @Test
    void generateStrategy_WithAge34_ShouldReturnAggressiveStrategy() {
        // Given
        Long userId = 10L;
        Integer age = 34;

        // When
        InvestmentStrategyDTO result = investmentStrategyService.generateStrategy(userId, age);

        // Then
        assertNotNull(result);
        assertEquals("Aggressive Growth", result.getCurrentStrategy());
        assertEquals("High", result.getRiskLevel());
        assertEquals(80, result.getStocksPercentage());
        assertEquals(15, result.getBondsPercentage());
        assertEquals(5, result.getCashPercentage());
    }

    @Test
    void generateStrategy_WithAge54_ShouldReturnModerateStrategy() {
        // Given
        Long userId = 11L;
        Integer age = 54;

        // When
        InvestmentStrategyDTO result = investmentStrategyService.generateStrategy(userId, age);

        // Then
        assertNotNull(result);
        assertEquals("Moderate Growth", result.getCurrentStrategy());
        assertEquals("Moderate", result.getRiskLevel());
        assertEquals(65, result.getStocksPercentage());
        assertEquals(30, result.getBondsPercentage());
        assertEquals(5, result.getCashPercentage());
    }

    @Test
    void generateStrategy_AllocationPercentages_ShouldSumTo100() {
        // Given
        Long userId = 12L;
        Integer age = 30;

        // When
        InvestmentStrategyDTO result = investmentStrategyService.generateStrategy(userId, age);

        // Then
        int totalPercentage = result.getStocksPercentage() + result.getBondsPercentage() + result.getCashPercentage();
        assertEquals(100, totalPercentage);
    }

    @Test
    void generateStrategy_WithVeryYoungAge_ShouldReturnAggressiveStrategy() {
        // Given
        Long userId = 13L;
        Integer age = 20;

        // When
        InvestmentStrategyDTO result = investmentStrategyService.generateStrategy(userId, age);

        // Then
        assertNotNull(result);
        assertEquals("Aggressive Growth", result.getCurrentStrategy());
        assertEquals("High", result.getRiskLevel());
        assertEquals(80, result.getStocksPercentage());
    }

    @Test
    void generateStrategy_WithVeryOldAge_ShouldReturnConservativeStrategy() {
        // Given
        Long userId = 14L;
        Integer age = 80;

        // When
        InvestmentStrategyDTO result = investmentStrategyService.generateStrategy(userId, age);

        // Then
        assertNotNull(result);
        assertEquals("Conservative", result.getCurrentStrategy());
        assertEquals("Low", result.getRiskLevel());
        assertEquals(40, result.getStocksPercentage());
    }
}
