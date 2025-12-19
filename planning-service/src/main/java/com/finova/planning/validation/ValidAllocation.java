package com.finova.planning.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validation annotation for investment allocation validation
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AllocationValidator.class)
@Documented
public @interface ValidAllocation {
    
    String message() default "Asset allocation percentages must sum to 100%";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
