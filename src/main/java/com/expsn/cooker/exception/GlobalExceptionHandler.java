package com.expsn.cooker.exception;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CookerException.class)
    public ResponseEntity<Map<String, String>> handleItem(CookerException ex) {
        return ResponseEntity.status(ex.getStatus()).body(Map.of("error", ex.getMessage()));
    }
}
