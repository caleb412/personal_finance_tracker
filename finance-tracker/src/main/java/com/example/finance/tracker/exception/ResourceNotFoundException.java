package com.example.finance.tracker.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Long id) {
        super("Resource not found with ID: " + id);
    }
}
