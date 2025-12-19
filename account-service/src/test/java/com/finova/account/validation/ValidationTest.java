package com.finova.account.validation;

import com.finova.account.dto.ContributionDTO;
import com.finova.account.dto.IncomeSourceDTO;
import com.finova.account.dto.RetirementAccountDTO;
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
 * Validation tests for Account Service DTOs
 */
@SpringBootTest
@ActiveProfiles("test")
class ValidationTest {

    @Autowired
    private Validator validator;

    @Test
    void validateRetirementAccountDTO_WithValidData_ShouldPass() {
        // Given
        RetirementAccountDTO account = new RetirementAccountDTO();
        account.setUserId(1L);
        account.setAccountName("My 401(k)");
        account.setAccountType("401k");
        account.setCurrentBalance(BigDecimal.valueOf(100000));
        account.setAsOfDate(LocalDate.now());
        account.setEstimatedMonthlyIncome(BigDecimal.valueOf(5000));
        account.setEstimatedMonthlyGoal(BigDecimal.valueOf(6000));

        // When
        Set<ConstraintViolation<RetirementAccountDTO>> violations = validator.validate(account);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void validateRetirementAccountDTO_WithInvalidAccountType_ShouldFail() {
        // Given
        RetirementAccountDTO account = new RetirementAccountDTO();
        account.setUserId(1L);
        account.setAccountName("My Account");
        account.setAccountType("INVALID_TYPE"); // Invalid type

        // When
        Set<ConstraintViolation<RetirementAccountDTO>> violations = validator.validate(account);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("accountType")));
    }

    @Test
    void validateRetirementAccountDTO_WithNegativeBalance_ShouldFail() {
        // Given
        RetirementAccountDTO account = new RetirementAccountDTO();
        account.setUserId(1L);
        account.setAccountName("My Account");
        account.setCurrentBalance(BigDecimal.valueOf(-1000)); // Negative balance

        // When
        Set<ConstraintViolation<RetirementAccountDTO>> violations = validator.validate(account);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("currentBalance")));
    }

    @Test
    void validateContributionDTO_WithValidData_ShouldPass() {
        // Given
        ContributionDTO contribution = new ContributionDTO();
        contribution.setAccountId(1L);
        contribution.setContributionType("pre_tax");
        contribution.setPercentage(BigDecimal.valueOf(10));
        contribution.setMonthlyAmount(BigDecimal.valueOf(1000));
        contribution.setAnnualAmount(BigDecimal.valueOf(12000));
        contribution.setEffectiveDate(LocalDate.now());

        // When
        Set<ConstraintViolation<ContributionDTO>> violations = validator.validate(contribution);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void validateContributionDTO_WithInvalidType_ShouldFail() {
        // Given
        ContributionDTO contribution = new ContributionDTO();
        contribution.setAccountId(1L);
        contribution.setContributionType("INVALID_TYPE"); // Invalid type

        // When
        Set<ConstraintViolation<ContributionDTO>> violations = validator.validate(contribution);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("contributionType")));
    }

    @Test
    void validateContributionDTO_WithInvalidPercentage_ShouldFail() {
        // Given
        ContributionDTO contribution = new ContributionDTO();
        contribution.setAccountId(1L);
        contribution.setContributionType("pre_tax");
        contribution.setPercentage(BigDecimal.valueOf(150)); // Invalid percentage > 100

        // When
        Set<ConstraintViolation<ContributionDTO>> violations = validator.validate(contribution);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("percentage")));
    }

    @Test
    void validateIncomeSourceDTO_WithValidData_ShouldPass() {
        // Given
        IncomeSourceDTO incomeSource = new IncomeSourceDTO();
        incomeSource.setAccountId(1L);
        incomeSource.setSourceType("401k");
        incomeSource.setSourceName("My 401(k) Withdrawals");
        incomeSource.setCurrentBalance(BigDecimal.valueOf(500000));
        incomeSource.setProjectedMonthlyIncome(BigDecimal.valueOf(2000));
        incomeSource.setProvider("Vanguard");

        // When
        Set<ConstraintViolation<IncomeSourceDTO>> violations = validator.validate(incomeSource);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void validateIncomeSourceDTO_WithInvalidSourceType_ShouldFail() {
        // Given
        IncomeSourceDTO incomeSource = new IncomeSourceDTO();
        incomeSource.setAccountId(1L);
        incomeSource.setSourceType("INVALID_TYPE"); // Invalid type

        // When
        Set<ConstraintViolation<IncomeSourceDTO>> violations = validator.validate(incomeSource);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("sourceType")));
    }

    @Test
    void validateIncomeSourceDTO_WithExcessiveBalance_ShouldFail() {
        // Given
        IncomeSourceDTO incomeSource = new IncomeSourceDTO();
        incomeSource.setAccountId(1L);
        incomeSource.setSourceType("401k");
        incomeSource.setCurrentBalance(BigDecimal.valueOf(15000000)); // Exceeds max

        // When
        Set<ConstraintViolation<IncomeSourceDTO>> violations = validator.validate(incomeSource);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("currentBalance")));
    }
}
