package com.clearbillsolutions.exception;

/**
 * Exception thrown for bad input requests or business logic violations.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
