package com.leanpay.loancalculator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LoanCalculatorExceptionHandler {

    private static final String message = "Failed due to unappropriated parameter value";

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleParameterValidation(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
}
