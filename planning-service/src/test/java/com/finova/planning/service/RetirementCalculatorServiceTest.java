package com.finova.planning.service;

import com.finova.planning.dto.RetirementPlanDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RetirementCalculatorService
 */
@SpringBootTest
class RetirementCalculatorServiceTest {

    @Autowired
    private RetirementCalculatorService retirementCalculatorService;

    private RetirementPlanDTO testPlan;

    @BeforeEach
    void setUp() {
        testPlan = new RetirementPlanDTO(1L, 42, 65);
    }

    @Test
    void calculateRetirementPlan_WithValidData_ShouldCalculateCorrectly() {
        // Given
        testPlan.setCurrentSavings(new BigDecimal("100000"));
        testPlan.setMonthlyContribution(new BigDecimal("1000"));
        testPlan.setEmployerMatch(new BigDecimal("500"));
        testPlan.setExpectedReturnRate(new BigDecimal("7.0"));
        testPlan.setDesiredMonthlyIncome(new BigDecimal("5000"));

        // When
        RetirementPlanDTO result = retirementCalculatorService.calculateRetirementPlan(testPlan);

        // Then
        assertNotNull(result);
        assertEquals(23, result.getYearsToRetirement());
        assertNotNull(result.getProjectedBalance());
        assertNotNull(result.getProjectedMonthlyIncome());
        assertNotNull(result.getStatus());
        assertNotNull(result.getRecommendations());
        assertTrue(result.getProjectedBalance().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(result.getProjectedMonthlyIncome().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void calculateRetirementPlan_WithNullValues_ShouldUseDefaults() {
        // Given
        RetirementPlanDTO planWithNulls = new RetirementPlanDTO(1L, 42, 65);
        // All other fields are null

        // When
        RetirementPlanDTO result = retirementCalculatorService.calculateRetirementPlan(planWithNulls);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("106965.67"), result.getCurrentSavings());
        assertEquals(new BigDecimal("650"), result.getMonthlyContribution());
        assertEquals(new BigDecimal("325"), result.getEmployerMatch());
        assertEquals(new BigDecimal("7.0"), result.getExpectedReturnRate());
        assertEquals(new BigDecimal("6200"), result.getDesiredMonthlyIncome());
        assertEquals(25, result.getExpectedRetirementDuration());
    }

    @Test
    void calculateRetirementPlan_WithZeroReturnRate_ShouldHandleCorrectly() {
        // Given
        testPlan.setCurrentSavings(new BigDecimal("100000"));
        testPlan.setMonthlyContribution(new BigDecimal("1000"));
        testPlan.setExpectedReturnRate(BigDecimal.ZERO);

        // When
        RetirementPlanDTO result = retirementCalculatorService.calculateRetirementPlan(testPlan);

        // Then
        assertNotNull(result);
        assertNotNull(result.getProjectedBalance());
        assertTrue(result.getProjectedBalance().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void calculateRetirementPlan_WithHighProjectedIncome_ShouldBeOnTrack() {
        // Given
        testPlan.setCurrentSavings(new BigDecimal("500000"));
        testPlan.setMonthlyContribution(new BigDecimal("2000"));
        testPlan.setEmployerMatch(new BigDecimal("1000"));
        testPlan.setExpectedReturnRate(new BigDecimal("8.0"));
        testPlan.setDesiredMonthlyIncome(new BigDecimal("3000"));

        // When
        RetirementPlanDTO result = retirementCalculatorService.calculateRetirementPlan(testPlan);

        // Then
        assertNotNull(result);
        assertTrue(result.getProjectedMonthlyIncome().compareTo(result.getDesiredMonthlyIncome()) >= 0);
        assertEquals("on_track", result.getStatus());
        assertTrue(result.getRecommendations().contains("Great job"));
    }

    @Test
    void calculateRetirementPlan_WithLowProjectedIncome_ShouldBeBehind() {
        // Given
        testPlan.setCurrentSavings(new BigDecimal("10000"));
        testPlan.setMonthlyContribution(new BigDecimal("100"));
        testPlan.setExpectedReturnRate(new BigDecimal("5.0"));
        testPlan.setDesiredMonthlyIncome(new BigDecimal("8000"));

        // When
        RetirementPlanDTO result = retirementCalculatorService.calculateRetirementPlan(testPlan);

        // Then
        assertNotNull(result);
        assertTrue(result.getProjectedMonthlyIncome().compareTo(result.getDesiredMonthlyIncome()) < 0);
        assertEquals("behind", result.getStatus());
        assertTrue(result.getRecommendations().contains("increasing your monthly contributions"));
    }

    @Test
    void calculateRetirementPlan_WithNullEmployerMatch_ShouldUseZero() {
        // Given
        testPlan.setCurrentSavings(new BigDecimal("100000"));
        testPlan.setMonthlyContribution(new BigDecimal("1000"));
        testPlan.setEmployerMatch(null);
        testPlan.setExpectedReturnRate(new BigDecimal("7.0"));

        // When
        RetirementPlanDTO result = retirementCalculatorService.calculateRetirementPlan(testPlan);

        // Then
        assertNotNull(result);
        assertNotNull(result.getProjectedBalance());
        // Should calculate correctly with zero employer match
        assertTrue(result.getProjectedBalance().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void calculateRetirementPlan_YearsToRetirementCalculation_ShouldBeCorrect() {
        // Given
        testPlan.setCurrentAge(30);
        testPlan.setRetirementAge(65);

        // When
        RetirementPlanDTO result = retirementCalculatorService.calculateRetirementPlan(testPlan);

        // Then
        assertEquals(35, result.getYearsToRetirement());
    }

    @Test
    void calculateRetirementPlan_ProjectedBalanceCalculation_ShouldBePositive() {
        // Given
        testPlan.setCurrentSavings(new BigDecimal("50000"));
        testPlan.setMonthlyContribution(new BigDecimal("500"));
        testPlan.setExpectedReturnRate(new BigDecimal("6.0"));

        // When
        RetirementPlanDTO result = retirementCalculatorService.calculateRetirementPlan(testPlan);

        // Then
        assertNotNull(result.getProjectedBalance());
        assertTrue(result.getProjectedBalance().compareTo(BigDecimal.ZERO) > 0);
        // Projected balance should be greater than current savings due to contributions and growth
        assertTrue(result.getProjectedBalance().compareTo(testPlan.getCurrentSavings()) > 0);
    }

    @Test
    void calculateRetirementPlan_ProjectedMonthlyIncomeCalculation_ShouldUseFourPercentRule() {
        // Given
        testPlan.setCurrentSavings(new BigDecimal("100000"));
        testPlan.setMonthlyContribution(new BigDecimal("1000"));
        testPlan.setExpectedReturnRate(new BigDecimal("7.0"));

        // When
        RetirementPlanDTO result = retirementCalculatorService.calculateRetirementPlan(testPlan);

        // Then
        assertNotNull(result.getProjectedMonthlyIncome());
        assertTrue(result.getProjectedMonthlyIncome().compareTo(BigDecimal.ZERO) > 0);
        
        // Verify 4% rule: monthly income should be approximately 4% of projected balance / 12
        BigDecimal expectedMonthlyIncome = result.getProjectedBalance()
            .multiply(new BigDecimal("0.04"))
            .divide(new BigDecimal("12"), 2, BigDecimal.ROUND_HALF_UP);
        
        // Allow for small rounding differences
        BigDecimal difference = result.getProjectedMonthlyIncome().subtract(expectedMonthlyIncome).abs();
        assertTrue(difference.compareTo(new BigDecimal("0.01")) <= 0);
    }

    @Test
    void calculateRetirementPlan_WithDifferentAges_ShouldCalculateCorrectly() {
        // Given - Young person
        RetirementPlanDTO youngPlan = new RetirementPlanDTO(1L, 25, 65);
        youngPlan.setCurrentSavings(new BigDecimal("10000"));
        youngPlan.setMonthlyContribution(new BigDecimal("500"));
        youngPlan.setExpectedReturnRate(new BigDecimal("8.0"));

        // When
        RetirementPlanDTO youngResult = retirementCalculatorService.calculateRetirementPlan(youngPlan);

        // Given - Older person
        RetirementPlanDTO oldPlan = new RetirementPlanDTO(2L, 55, 65);
        oldPlan.setCurrentSavings(new BigDecimal("200000"));
        oldPlan.setMonthlyContribution(new BigDecimal("2000"));
        oldPlan.setExpectedReturnRate(new BigDecimal("6.0"));

        // When
        RetirementPlanDTO oldResult = retirementCalculatorService.calculateRetirementPlan(oldPlan);

        // Then
        assertEquals(40, youngResult.getYearsToRetirement());
        assertEquals(10, oldResult.getYearsToRetirement());
        
        // Young person should have more time for compound growth
        assertTrue(youngResult.getProjectedBalance().compareTo(oldResult.getProjectedBalance()) > 0);
    }
}
