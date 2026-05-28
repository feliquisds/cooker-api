package com.expsn.cooker.exception;

import org.springframework.http.HttpStatus;

public class ItemException extends RuntimeException {

    private final HttpStatus status;

    public ItemException(String message) {
        this(message, HttpStatus.PRECONDITION_FAILED);
    }

    public ItemException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}