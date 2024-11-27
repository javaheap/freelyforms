package com.utbm.da50.freelyform.exceptions;

/**
 * Exception thrown when validation fails in the application.
 */
public class ValidationException extends RuntimeException {
    /**
     * Constructs a new ValidationException with the specified detail message.
     *
     * @param message the detail message, which is saved for later retrieval
     *                by the {@link Throwable#getMessage()} method
     */
    public ValidationException(String message) {
        super(message);
    }
}