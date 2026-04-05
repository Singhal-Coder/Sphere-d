package com.fareye.sphere.d.annotations;

import com.fareye.sphere.d.validators.FutureBookingLimitValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FutureBookingLimitValidator.class)
@Documented
public @interface FutureBookingLimit {
    String message() default "Booking can only be done up to 30 days in advance.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}