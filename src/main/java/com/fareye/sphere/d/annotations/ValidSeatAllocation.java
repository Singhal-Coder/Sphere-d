package com.fareye.sphere.d.annotations;

import com.fareye.sphere.d.validators.DepartmentMatchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DepartmentMatchValidator.class)
@Documented
public @interface ValidSeatAllocation {
    String message() default "You can only book your department seat.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}