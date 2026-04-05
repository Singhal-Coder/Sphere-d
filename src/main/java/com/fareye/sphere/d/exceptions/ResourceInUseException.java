package com.fareye.sphere.d.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceInUseException extends BaseException {
    public ResourceInUseException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}