package com.fareye.sphere.d.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidIdException extends BaseException {
    public InvalidIdException(String id) {
        super(String.format("The provided ID '%s' is not in a valid format.", id), HttpStatus.BAD_REQUEST);
    }
}