package com.fareye.sphere.d.validators;

import com.fareye.sphere.d.annotations.ValidPassword;
import com.fareye.sphere.d.utils.PasswordUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Autowired
    private PasswordUtils passwordUtils;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value==null) return true;
        return passwordUtils.validatePassword(value);
    }
}