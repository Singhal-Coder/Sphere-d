package com.fareye.sphere.d.validators;

import com.fareye.sphere.d.annotations.ValidFormattedId;
import com.fareye.sphere.d.utils.IdUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class FormattedIdValidator implements ConstraintValidator<ValidFormattedId, String> {

    @Autowired
    private IdUtils idUtils;

    private String idType;

    @Override
    public void initialize(ValidFormattedId constraintAnnotation) {
        this.idType = constraintAnnotation.type().toUpperCase();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return switch (idType) {
            case "USER" -> idUtils.validateUserId(value);
            case "ASSET" -> idUtils.validateSerialNumber(value);
            case "BOOKING" -> idUtils.validateBookingId(value);
            case "SEAT" -> idUtils.validateSeatId(value);
            case "REQUEST" -> idUtils.validateRequestId(value);
            case "REQUEST_LOG" -> idUtils.validateRequestLogId(value);
            default -> false;
        };
    }
}