package com.fareye.sphere.d.entities.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AssetStatus {
    AVAILABLE,
    ASSIGNED,
    BROKEN,
    ;

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}
