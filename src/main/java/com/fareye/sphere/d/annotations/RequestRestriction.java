package com.fareye.sphere.d.annotations;

import com.fareye.sphere.d.validators.EmployeeRequestPolicyValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmployeeRequestPolicyValidator.class)
public @interface RequestRestriction {
    String message() default "Employee can't change status.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
