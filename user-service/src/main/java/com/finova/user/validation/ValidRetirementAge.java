package com.finova.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validation annotation for retirement age validation
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RetirementAgeValidator.class)
@Documented
public @interface ValidRetirementAge {
    
    String message() default "Retirement age must be between 55 and 75 and greater than current age";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
