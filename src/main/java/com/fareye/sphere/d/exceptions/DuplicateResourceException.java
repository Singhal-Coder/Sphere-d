package com.fareye.sphere.d.exceptions;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends BaseException{
    public DuplicateResourceException(String resource, String field, String value) {
        super(String.format("%s already exists with %s: '%s'", resource, field, value), HttpStatus.CONFLICT);
    }
}