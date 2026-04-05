package com.fareye.sphere.d.validators;

import com.fareye.sphere.d.annotations.ValidSeatAllocation;
import com.fareye.sphere.d.entities.Booking;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DepartmentMatchValidator implements ConstraintValidator<ValidSeatAllocation, Booking> {

    @Override
    public boolean isValid(Booking value, ConstraintValidatorContext context) {
        if (value == null || value.getUser() == null || value.getSeat() == null) return false;
        return value.getUser().getDepartment()==value.getSeat().getDepartment();
    }
}