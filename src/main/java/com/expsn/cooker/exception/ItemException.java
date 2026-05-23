package com.expsn.cooker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class ItemException extends RuntimeException {
    public ItemException(String message) {
        super(message);
    }
}
