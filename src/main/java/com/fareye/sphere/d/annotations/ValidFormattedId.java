package com.fareye.sphere.d.annotations;

import com.fareye.sphere.d.validators.FormattedIdValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, TYPE_USE, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = FormattedIdValidator.class)
public @interface ValidFormattedId {
    String message() default "Invalid ID format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String type();
}