package com.bank.pfe1.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        Map<String, String> response = new HashMap<>();

        if (e.getMessage().contains("Plate number already exists")) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        if (e.getMessage().contains("Cannot delete vehicle")) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        if (e.getMessage().contains("not found")) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
