package com.fareye.sphere.d.validators;

import com.fareye.sphere.d.annotations.RequestRestriction;
import com.fareye.sphere.d.entities.Request;
import com.fareye.sphere.d.entities.enums.RequestStatus;
import com.fareye.sphere.d.entities.enums.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmployeeRequestPolicyValidator implements ConstraintValidator<RequestRestriction, Request> {

    @Override
    public boolean isValid(Request value, ConstraintValidatorContext context) {
        if (value==null || value.getLastModifier()==null) return false;
        return value.getLastModifier().getRole()!=Role.EMPLOYEE ||
                value.getStatus()==RequestStatus.DRAFT ||
                value.getStatus()==RequestStatus.PENDING;
    }
}