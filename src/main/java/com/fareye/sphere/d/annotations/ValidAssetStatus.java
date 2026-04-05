package com.fareye.sphere.d.annotations;

import com.fareye.sphere.d.validators.AssetDtoAssignmentValidator;
import com.fareye.sphere.d.validators.AssetAssignmentValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {AssetAssignmentValidator.class, AssetDtoAssignmentValidator.class})
public @interface ValidAssetStatus {
    String message() default "Assets can't be available and assigned at same time.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}