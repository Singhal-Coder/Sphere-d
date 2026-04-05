package com.fareye.sphere.d.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidWorkflowStateException extends BaseException {
    public InvalidWorkflowStateException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}