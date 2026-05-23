package com.expsn.cooker.exception;

import org.springframework.http.HttpStatus;

public class CookerException extends RuntimeException {

    private final HttpStatus status;

    public CookerException(String message) {
        super(message);
        this.status = HttpStatus.PRECONDITION_FAILED;
    }

    public CookerException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
