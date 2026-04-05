package com.fareye.sphere.d.entities.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SeatStatus {
    AVAILABLE,
    UNAVAILABLE
    ;
    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}