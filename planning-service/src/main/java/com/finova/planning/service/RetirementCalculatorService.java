package com.finova.planning.service;

import com.finova.planning.dto.RetirementPlanDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Service for retirement planning calculations
 */
@Service
public class RetirementCalculatorService {
    
    /**
     * Calculate retirement projections
     */
    public RetirementPlanDTO calculateRetirementPlan(RetirementPlanDTO planDTO) {
        // Set default values if not provided
        setDefaults(planDTO);
        
        // Calculate years to retirement
        int yearsToRetirement = planDTO.getRetirementAge() - planDTO.getCurrentAge();
        planDTO.setYearsToRetirement(yearsToRetirement);
        
        // Calculate total monthly contributions
        BigDecimal totalMonthlyContribution = planDTO.getMonthlyContribution()
            .add(planDTO.getEmployerMatch() != null ? planDTO.getEmployerMatch() : BigDecimal.ZERO);
        
        // Calculate projected balance using compound interest formula
        BigDecimal monthlyRate = planDTO.getExpectedReturnRate().divide(new BigDecimal("1200"), 10, RoundingMode.HALF_UP);
        int totalMonths = yearsToRetirement * 12;
        
        // Future value of current savings
        BigDecimal futureValueCurrentSavings = planDTO.getCurrentSavings()
            .multiply(BigDecimal.ONE.add(monthlyRate).pow(totalMonths));
        
        // Future value of monthly contributions
        BigDecimal futureValueContributions;
        if (monthlyRate.compareTo(BigDecimal.ZERO) > 0) {
            futureValueContributions = totalMonthlyContribution
                .multiply(BigDecimal.ONE.add(monthlyRate).pow(totalMonths).subtract(BigDecimal.ONE))
                .divide(monthlyRate, 2, RoundingMode.HALF_UP);
        } else {
            futureValueContributions = totalMonthlyContribution.multiply(new BigDecimal(totalMonths));
        }
        
        BigDecimal projectedBalance = futureValueCurrentSavings.add(futureValueContributions);
        planDTO.setProjectedBalance(projectedBalance);
        
        // Calculate projected monthly income (4% rule)
        BigDecimal projectedMonthlyIncome = projectedBalance
            .multiply(new BigDecimal("0.04"))
            .divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
        planDTO.setProjectedMonthlyIncome(projectedMonthlyIncome);
        
        // Determine status
        determineStatus(planDTO);
        
        return planDTO;
    }
    
    private void setDefaults(RetirementPlanDTO planDTO) {
        if (planDTO.getCurrentSavings() == null) {
            planDTO.setCurrentSavings(new BigDecimal("106965.67"));
        }
        if (planDTO.getMonthlyContribution() == null) {
            planDTO.setMonthlyContribution(new BigDecimal("650"));
        }
        if (planDTO.getEmployerMatch() == null) {
            planDTO.setEmployerMatch(new BigDecimal("325"));
        }
        if (planDTO.getExpectedReturnRate() == null) {
            planDTO.setExpectedReturnRate(new BigDecimal("7.0"));
        }
        if (planDTO.getDesiredMonthlyIncome() == null) {
            planDTO.setDesiredMonthlyIncome(new BigDecimal("6200"));
        }
        if (planDTO.getExpectedRetirementDuration() == null) {
            planDTO.setExpectedRetirementDuration(25);
        }
    }
    
    private void determineStatus(RetirementPlanDTO planDTO) {
        if (planDTO.getProjectedMonthlyIncome() != null && planDTO.getDesiredMonthlyIncome() != null) {
            int comparison = planDTO.getProjectedMonthlyIncome().compareTo(planDTO.getDesiredMonthlyIncome());
            if (comparison >= 0) {
                planDTO.setStatus("on_track");
                planDTO.setRecommendations("Great job! You're on track to meet your retirement goals.");
            } else {
                planDTO.setStatus("behind");
                planDTO.setRecommendations("Consider increasing your monthly contributions to meet your retirement goals.");
            }
        }
    }
}
