package com.audrina.eccommerce.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    public ResponseEntity<String> productNotFoundException( Exception exception) {


    }
}
