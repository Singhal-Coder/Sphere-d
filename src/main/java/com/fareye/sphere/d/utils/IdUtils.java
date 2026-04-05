package com.fareye.sphere.d.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.Optional;

import static java.util.regex.Pattern.quote;

@Component
@ApplicationScope
public class IdUtils {

    private static final String USER_PREFIX = "FEI-";
    private static final String ASSET_PREFIX = "FEAS-";
    private static final String BOOKING_PREFIX = "FEBS-";
    private static final String SEAT_PREFIX = "FESI-";
    private static final String REQUEST_PREFIX = "FERA-";
    private static final String REQUEST_LOG_PREFIX = "FERL-";
    private static final int PADDING_LENGTH=6;

    private Optional<String> format(Long id, String prefix) {
        if (id == null) return Optional.empty();
        return Optional.of(String.format("%s%0"+PADDING_LENGTH+"d", prefix, id));
    }

    private Optional<Long> parse(String formattedId, String prefix) {
        if (formattedId == null || !formattedId.startsWith(prefix)) return Optional.empty();
        try {
            return Optional.of(Long.parseLong(formattedId.substring(prefix.length())));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private boolean validate(String id, String prefix) {
        return id != null && id.matches("^" + quote(prefix) + "\\d{" + PADDING_LENGTH + "}$");
    }


    public Optional<String> formatUserId(Long id) { return format(id, USER_PREFIX); }
    public Optional<Long> parseUserId(String id) { return parse(id, USER_PREFIX); }

    public Optional<String> formatSerialNumber(Long id) { return format(id, ASSET_PREFIX); }
    public Optional<Long> parseSerialNumber(String id) { return parse(id, ASSET_PREFIX); }

    public Optional<String> formatBookingId(Long id) { return format(id, BOOKING_PREFIX); }
    public Optional<Long> parseBookingId(String id) { return parse(id, BOOKING_PREFIX); }

    public Optional<String> formatSeatId(Long id) { return format(id, SEAT_PREFIX); }
    public Optional<Long> parseSeatId(String id) { return parse(id, SEAT_PREFIX); }

    public Optional<String> formatRequestId(Long id) { return format(id, REQUEST_PREFIX); }
    public Optional<Long> parseRequestId(String id) { return parse(id, REQUEST_PREFIX); }

    public Optional<String> formatRequestLogId(Long id) { return format(id, REQUEST_LOG_PREFIX); }
    public Optional<Long> parseRequestLogId(String id) { return parse(id, REQUEST_LOG_PREFIX); }


    public boolean validateUserId(String id){ return validate(id, USER_PREFIX); }
    public boolean validateSerialNumber(String id){ return validate(id, ASSET_PREFIX); }
    public boolean validateBookingId(String id){ return validate(id, BOOKING_PREFIX); }
    public boolean validateSeatId(String id){ return validate(id, SEAT_PREFIX); }
    public boolean validateRequestId(String id){ return validate(id, REQUEST_PREFIX); }
    public boolean validateRequestLogId(String id){ return validate(id, REQUEST_LOG_PREFIX); }
}