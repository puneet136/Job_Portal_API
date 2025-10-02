package com.api.Job_Portal.Exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Optional: Add a no-args constructor if needed by frameworks
    public ResourceNotFoundException() {
        super();
    }
}