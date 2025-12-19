package com.finova.planning.validation;

import com.finova.planning.dto.InvestmentStrategyDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for investment allocation business rules
 */
public class AllocationValidator implements ConstraintValidator<ValidAllocation, InvestmentStrategyDTO> {
    
    @Override
    public void initialize(ValidAllocation constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(InvestmentStrategyDTO strategy, ConstraintValidatorContext context) {
        if (strategy == null) {
            return true; // Let other validators handle null checks
        }
        
        Integer stocks = strategy.getStocksPercentage();
        Integer bonds = strategy.getBondsPercentage();
        Integer cash = strategy.getCashPercentage();
        
        // If any percentage is null, skip validation (let other validators handle it)
        if (stocks == null || bonds == null || cash == null) {
            return true;
        }
        
        // Check if percentages sum to 100
        int total = stocks + bonds + cash;
        
        if (total != 100) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                String.format("Asset allocation percentages must sum to 100% (currently %d%%)", total)
            ).addConstraintViolation();
            return false;
        }
        
        return true;
    }
}
