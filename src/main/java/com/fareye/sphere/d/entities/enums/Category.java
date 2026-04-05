package com.fareye.sphere.d.entities.enums;


import com.fasterxml.jackson.annotation.JsonValue;

public enum Category {
    BAG,
    CHARGER,
    HEADPHONE,
    LAPTOP,
    NOTEBOOK,
    PHONE,
    SOFTWARE,
    ;

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}