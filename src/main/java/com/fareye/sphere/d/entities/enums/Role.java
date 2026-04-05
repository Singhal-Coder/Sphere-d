package com.fareye.sphere.d.entities.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    ADMIN,
    EMPLOYEE,
    IT_SUPPORT_MEMBER,
    SYSTEM,
    ;
    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}