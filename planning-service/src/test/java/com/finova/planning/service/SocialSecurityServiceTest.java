package com.finova.planning.service;

import com.finova.planning.dto.SocialSecurityDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SocialSecurityService
 */
@SpringBootTest
class SocialSecurityServiceTest {

    @Autowired
    private SocialSecurityService socialSecurityService;

    private SocialSecurityDTO testSS;

    @BeforeEach
    void setUp() {
        testSS = new SocialSecurityDTO();
        testSS.setUserId(1L);
    }

    @Test
    void calculateBenefits_WithValidData_ShouldCalculateCorrectly() {
        // Given
        testSS.setDateOfBirth(LocalDate.of(1980, 5, 15));
        testSS.setCurrentSalary(new BigDecimal("75000"));
        testSS.setYearsOfWorkHistory(15);

        // When
        SocialSecurityDTO result = socialSecurityService.calculateBenefits(testSS);

        // Then
        assertNotNull(result);
        assertNotNull(result.getBenefitAt62());
        assertNotNull(result.getBenefitAt67());
        assertNotNull(result.getBenefitAt70());
        assertEquals(67, result.getFullRetirementAge());
        assertNotNull(result.getRecommendations());
        
        // Verify benefit calculations
        assertTrue(result.getBenefitAt62().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(result.getBenefitAt67().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(result.getBenefitAt70().compareTo(BigDecimal.ZERO) > 0);
        
        // Benefit at 67 should be higher than at 62
        assertTrue(result.getBenefitAt67().compareTo(result.getBenefitAt62()) > 0);
        
        // Benefit at 70 should be higher than at 67
        assertTrue(result.getBenefitAt70().compareTo(result.getBenefitAt67()) > 0);
    }

    @Test
    void calculateBenefits_WithNullValues_ShouldUseDefaults() {
        // Given
        SocialSecurityDTO planWithNulls = new SocialSecurityDTO();
        planWithNulls.setUserId(1L);
        // All other fields are null

        // When
        SocialSecurityDTO result = socialSecurityService.calculateBenefits(planWithNulls);

        // Then
        assertNotNull(result);
        assertEquals(LocalDate.of(1983, 5, 15), result.getDateOfBirth());
        assertEquals(new BigDecimal("78000"), result.getCurrentSalary());
        assertEquals(20, result.getYearsOfWorkHistory());
    }

    @Test
    void calculateBenefits_WithHighSalary_ShouldCalculateHigherBenefits() {
        // Given
        testSS.setCurrentSalary(new BigDecimal("150000"));
        testSS.setDateOfBirth(LocalDate.of(1984, 1, 1)); // Age 40

        // When
        SocialSecurityDTO highSalaryResult = socialSecurityService.calculateBenefits(testSS);

        // Given - Lower salary
        SocialSecurityDTO lowSalarySS = new SocialSecurityDTO();
        lowSalarySS.setUserId(2L);
        lowSalarySS.setCurrentSalary(new BigDecimal("30000"));
        lowSalarySS.setDateOfBirth(LocalDate.of(1984, 1, 1)); // Age 40

        // When
        SocialSecurityDTO lowSalaryResult = socialSecurityService.calculateBenefits(lowSalarySS);

        // Then
        assertTrue(highSalaryResult.getBenefitAt67().compareTo(lowSalaryResult.getBenefitAt67()) > 0);
    }

    @Test
    void calculateBenefits_WithYoungPerson_ShouldGenerateAppropriateRecommendations() {
        // Given
        testSS.setDateOfBirth(LocalDate.of(1994, 1, 1)); // Age 30
        testSS.setCurrentSalary(new BigDecimal("60000"));

        // When
        SocialSecurityDTO result = socialSecurityService.calculateBenefits(testSS);

        // Then
        assertNotNull(result.getRecommendations());
        assertTrue(result.getRecommendations().contains("Continue working"));
        assertTrue(result.getRecommendations().contains("building your earnings record"));
    }

    @Test
    void calculateBenefits_WithPersonNearRetirement_ShouldGenerateAppropriateRecommendations() {
        // Given
        testSS.setDateOfBirth(LocalDate.of(1960, 1, 1)); // Age 64
        testSS.setCurrentSalary(new BigDecimal("80000"));

        // When
        SocialSecurityDTO result = socialSecurityService.calculateBenefits(testSS);

        // Then
        assertNotNull(result.getRecommendations());
        assertTrue(result.getRecommendations().contains("reduced benefits"));
        assertTrue(result.getRecommendations().contains("full retirement age"));
    }

    @Test
    void calculateBenefits_WithPersonAtRetirementAge_ShouldGenerateAppropriateRecommendations() {
        // Given
        testSS.setDateOfBirth(LocalDate.of(1956, 1, 1)); // Age 68
        testSS.setCurrentSalary(new BigDecimal("70000"));

        // When
        SocialSecurityDTO result = socialSecurityService.calculateBenefits(testSS);

        // Then
        assertNotNull(result.getRecommendations());
        assertTrue(result.getRecommendations().contains("full retirement age"));
        assertTrue(result.getRecommendations().contains("Delaying until age 70"));
    }

    @Test
    void calculateBenefits_BenefitCalculation_ShouldUseFortyPercentRule() {
        // Given
        testSS.setCurrentSalary(new BigDecimal("120000"));
        testSS.setDateOfBirth(LocalDate.of(1979, 1, 1)); // Age 45

        // When
        SocialSecurityDTO result = socialSecurityService.calculateBenefits(testSS);

        // Then
        // Base benefit should be approximately 40% of salary / 12
        BigDecimal expectedBaseBenefit = new BigDecimal("120000")
            .multiply(new BigDecimal("0.40"))
            .divide(new BigDecimal("12"), BigDecimal.ROUND_HALF_UP);
        
        // Allow for small rounding differences
        BigDecimal difference = result.getBenefitAt67().subtract(expectedBaseBenefit).abs();
        assertTrue(difference.compareTo(new BigDecimal("0.01")) <= 0);
    }

    @Test
    void calculateBenefits_BenefitAt62_ShouldBeSeventyFivePercent() {
        // Given
        testSS.setCurrentSalary(new BigDecimal("100000"));
        testSS.setDateOfBirth(LocalDate.of(1979, 1, 1)); // Age 45

        // When
        SocialSecurityDTO result = socialSecurityService.calculateBenefits(testSS);

        // Then
        // Benefit at 62 should be 75% of benefit at 67
        BigDecimal expectedBenefitAt62 = result.getBenefitAt67().multiply(new BigDecimal("0.75"));
        
        // Allow for small rounding differences
        BigDecimal difference = result.getBenefitAt62().subtract(expectedBenefitAt62).abs();
        assertTrue(difference.compareTo(new BigDecimal("0.01")) <= 0);
    }

    @Test
    void calculateBenefits_BenefitAt70_ShouldBeOneHundredThirtyTwoPercent() {
        // Given
        testSS.setCurrentSalary(new BigDecimal("100000"));
        testSS.setDateOfBirth(LocalDate.of(1979, 1, 1)); // Age 45

        // When
        SocialSecurityDTO result = socialSecurityService.calculateBenefits(testSS);

        // Then
        // Benefit at 70 should be 132% of benefit at 67
        BigDecimal expectedBenefitAt70 = result.getBenefitAt67().multiply(new BigDecimal("1.32"));
        
        // Allow for small rounding differences
        BigDecimal difference = result.getBenefitAt70().subtract(expectedBenefitAt70).abs();
        assertTrue(difference.compareTo(new BigDecimal("0.01")) <= 0);
    }

    @Test
    void calculateBenefits_WithNullDateOfBirth_ShouldNotGenerateRecommendations() {
        // Given
        testSS.setCurrentSalary(new BigDecimal("60000"));
        testSS.setDateOfBirth(null);

        // When
        SocialSecurityDTO result = socialSecurityService.calculateBenefits(testSS);

        // Then
        assertNotNull(result);
        assertNull(result.getRecommendations());
    }

    @Test
    void calculateBenefits_FullRetirementAge_ShouldAlwaysBe67() {
        // Given
        testSS.setCurrentSalary(new BigDecimal("50000"));
        testSS.setDateOfBirth(LocalDate.of(1994, 1, 1)); // Age 30

        // When
        SocialSecurityDTO result = socialSecurityService.calculateBenefits(testSS);

        // Then
        assertEquals(67, result.getFullRetirementAge());
    }
}
