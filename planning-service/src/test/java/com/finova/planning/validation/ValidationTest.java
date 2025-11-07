package com.finova.planning.validation;

import com.finova.planning.dto.InvestmentStrategyDTO;
import com.finova.planning.dto.RetirementPlanDTO;
import com.finova.planning.dto.SocialSecurityDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validation tests for Planning Service DTOs
 */
@SpringBootTest
@ActiveProfiles("test")
class ValidationTest {

    @Autowired
    private Validator validator;

    @Test
    void validateRetirementPlanDTO_WithValidData_ShouldPass() {
        // Given
        RetirementPlanDTO plan = new RetirementPlanDTO();
        plan.setUserId(1L);
        plan.setCurrentAge(42);
        plan.setRetirementAge(65);
        plan.setExpectedRetirementDuration(20);
        plan.setCurrentSavings(BigDecimal.valueOf(100000));
        plan.setMonthlyContribution(BigDecimal.valueOf(1000));
        plan.setEmployerMatch(BigDecimal.valueOf(5));
        plan.setDesiredMonthlyIncome(BigDecimal.valueOf(5000));
        plan.setExpectedReturnRate(BigDecimal.valueOf(7));
        plan.setExpectedInflationRate(BigDecimal.valueOf(3));

        // When
        Set<ConstraintViolation<RetirementPlanDTO>> violations = validator.validate(plan);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void validateRetirementPlanDTO_WithInvalidAge_ShouldFail() {
        // Given
        RetirementPlanDTO plan = new RetirementPlanDTO();
        plan.setUserId(1L);
        plan.setCurrentAge(15); // Too young
        plan.setRetirementAge(65);

        // When
        Set<ConstraintViolation<RetirementPlanDTO>> violations = validator.validate(plan);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("currentAge")));
    }

    @Test
    void validateRetirementPlanDTO_WithInvalidRetirementAge_ShouldFail() {
        // Given
        RetirementPlanDTO plan = new RetirementPlanDTO();
        plan.setUserId(1L);
        plan.setCurrentAge(42);
        plan.setRetirementAge(50); // Too young for retirement

        // When
        Set<ConstraintViolation<RetirementPlanDTO>> violations = validator.validate(plan);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("retirementAge")));
    }

    @Test
    void validateRetirementPlanDTO_WithExcessiveReturnRate_ShouldFail() {
        // Given
        RetirementPlanDTO plan = new RetirementPlanDTO();
        plan.setUserId(1L);
        plan.setCurrentAge(42);
        plan.setRetirementAge(65);
        plan.setExpectedReturnRate(BigDecimal.valueOf(75)); // Too high

        // When
        Set<ConstraintViolation<RetirementPlanDTO>> violations = validator.validate(plan);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("expectedReturnRate")));
    }

    @Test
    void validateSocialSecurityDTO_WithValidData_ShouldPass() {
        // Given
        SocialSecurityDTO ss = new SocialSecurityDTO();
        ss.setUserId(1L);
        ss.setDateOfBirth(LocalDate.of(1980, 1, 1));
        ss.setCurrentSalary(BigDecimal.valueOf(75000));
        ss.setYearsOfWorkHistory(20);

        // When
        Set<ConstraintViolation<SocialSecurityDTO>> violations = validator.validate(ss);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void validateSocialSecurityDTO_WithFutureDateOfBirth_ShouldFail() {
        // Given
        SocialSecurityDTO ss = new SocialSecurityDTO();
        ss.setUserId(1L);
        ss.setDateOfBirth(LocalDate.now().plusDays(1)); // Future date

        // When
        Set<ConstraintViolation<SocialSecurityDTO>> violations = validator.validate(ss);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("dateOfBirth")));
    }

    @Test
    void validateSocialSecurityDTO_WithExcessiveSalary_ShouldFail() {
        // Given
        SocialSecurityDTO ss = new SocialSecurityDTO();
        ss.setUserId(1L);
        ss.setDateOfBirth(LocalDate.of(1980, 1, 1));
        ss.setCurrentSalary(BigDecimal.valueOf(2000000)); // Too high

        // When
        Set<ConstraintViolation<SocialSecurityDTO>> violations = validator.validate(ss);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("currentSalary")));
    }

    @Test
    void validateInvestmentStrategyDTO_WithValidData_ShouldPass() {
        // Given
        InvestmentStrategyDTO strategy = new InvestmentStrategyDTO();
        strategy.setUserId(1L);
        strategy.setPortfolioValue(BigDecimal.valueOf(500000));
        strategy.setCurrentStrategy("Moderate Growth");
        strategy.setRiskLevel("Moderate");
        strategy.setStocksPercentage(60);
        strategy.setBondsPercentage(30);
        strategy.setCashPercentage(10);

        // When
        Set<ConstraintViolation<InvestmentStrategyDTO>> violations = validator.validate(strategy);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void validateInvestmentStrategyDTO_WithInvalidStrategy_ShouldFail() {
        // Given
        InvestmentStrategyDTO strategy = new InvestmentStrategyDTO();
        strategy.setUserId(1L);
        strategy.setCurrentStrategy("INVALID_STRATEGY"); // Invalid strategy

        // When
        Set<ConstraintViolation<InvestmentStrategyDTO>> violations = validator.validate(strategy);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("currentStrategy")));
    }

    @Test
    void validateInvestmentStrategyDTO_WithInvalidRiskLevel_ShouldFail() {
        // Given
        InvestmentStrategyDTO strategy = new InvestmentStrategyDTO();
        strategy.setUserId(1L);
        strategy.setRiskLevel("INVALID_RISK"); // Invalid risk level

        // When
        Set<ConstraintViolation<InvestmentStrategyDTO>> violations = validator.validate(strategy);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("riskLevel")));
    }

    @Test
    void validateInvestmentStrategyDTO_WithInvalidAllocation_ShouldFail() {
        // Given
        InvestmentStrategyDTO strategy = new InvestmentStrategyDTO();
        strategy.setUserId(1L);
        strategy.setStocksPercentage(50);
        strategy.setBondsPercentage(30);
        strategy.setCashPercentage(10); // Total = 90%, should be 100%

        // When
        Set<ConstraintViolation<InvestmentStrategyDTO>> violations = validator.validate(strategy);

        // Then
        assertFalse(violations.isEmpty());
        // The custom validator should catch this
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("sum to 100%")));
    }

    @Test
    void validateInvestmentStrategyDTO_WithExcessivePortfolioValue_ShouldFail() {
        // Given
        InvestmentStrategyDTO strategy = new InvestmentStrategyDTO();
        strategy.setUserId(1L);
        strategy.setPortfolioValue(BigDecimal.valueOf(15000000)); // Exceeds max

        // When
        Set<ConstraintViolation<InvestmentStrategyDTO>> violations = validator.validate(strategy);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("portfolioValue")));
    }
}
