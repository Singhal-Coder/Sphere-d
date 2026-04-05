package com.fareye.sphere.d.entities.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Department {
    IT,
    PROJECT,
    CULTURE,
    ;
    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}
