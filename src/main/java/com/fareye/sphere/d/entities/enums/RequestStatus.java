package com.fareye.sphere.d.entities.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RequestStatus {
    APPROVED,
    ASSIGNED,
    DRAFT,
    PENDING,
    REJECTED,
    ;

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}