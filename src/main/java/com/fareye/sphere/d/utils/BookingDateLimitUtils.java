package com.fareye.sphere.d.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.time.LocalDate;

@Component
@ApplicationScope
public class BookingDateLimitUtils {
    private static final int MAX_ADVANCE_BOOKING_DAYS=30;
    public boolean isDateAllowed(LocalDate bookedDate){
        if (bookedDate == null) return false;

        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusDays(MAX_ADVANCE_BOOKING_DAYS);

        return !bookedDate.isBefore(today) && !bookedDate.isAfter(maxDate);
    }
}