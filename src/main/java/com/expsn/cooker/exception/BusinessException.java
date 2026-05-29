package com.expsn.cooker.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.PRECONDITION_FAILED;
    }

    public BusinessException(String message) {
        this(message, HttpStatus.PRECONDITION_FAILED);
    }

    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}