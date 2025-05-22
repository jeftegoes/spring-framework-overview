package com.example.handlers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

//@ControllerAdvice
public class GlobalExceptionHandler {
    // Handle specific exception
    @ExceptionHandler(Exception.class)
    public void handleAllExceptions(Exception ex) {
        // Capture the exception without returning anything
        captureError(ex);
        // Optionally, you can also log it or send it to an external service
        // For example: log.error("An error occurred: {}", ex.getMessage());
    }

    private void captureError(Exception ex) {
        // Implement your error capture logic here
        // For example, log the exception or send it to a monitoring system
        System.err.println("Captured Exception: " + ex.getMessage());
    }
}
