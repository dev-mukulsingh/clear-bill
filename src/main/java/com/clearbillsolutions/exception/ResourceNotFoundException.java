package com.clearbillsolutions.exception;

/**
 * Exception thrown when an entity/resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
