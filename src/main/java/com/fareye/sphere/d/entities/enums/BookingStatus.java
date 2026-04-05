package com.fareye.sphere.d.entities.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BookingStatus {
    ACTIVE,
    CANCELLED,
    ;
    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}
