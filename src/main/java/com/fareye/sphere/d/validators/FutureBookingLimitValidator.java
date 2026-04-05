package com.fareye.sphere.d.validators;

import com.fareye.sphere.d.annotations.FutureBookingLimit;
import com.fareye.sphere.d.utils.BookingDateLimitUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

public class FutureBookingLimitValidator implements ConstraintValidator<FutureBookingLimit, LocalDate> {
    @Autowired
    private BookingDateLimitUtils bookingDateLimitUtils;

    @Override
    public boolean isValid(LocalDate bookedDate, ConstraintValidatorContext context) {
        return bookingDateLimitUtils.isDateAllowed(bookedDate);
    }
}