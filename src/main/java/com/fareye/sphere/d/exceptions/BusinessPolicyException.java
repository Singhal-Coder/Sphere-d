package com.fareye.sphere.d.exceptions;

import org.springframework.http.HttpStatus;

public class BusinessPolicyException extends BaseException {
    public BusinessPolicyException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}