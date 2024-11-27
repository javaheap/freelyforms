package com.utbm.da50.freelyform.exceptions;

/**
 * Exception thrown when a unique response constraint is violated.
 */
public class UniqueResponseException extends RuntimeException {
    /**
     * Constructs a new UniqueResponseException with the specified detail message.
     *
     * @param message the detail message, which is saved for later retrieval
     *                by the {@link Throwable#getMessage()} method
     */
    public UniqueResponseException(String message) {
        super(message);
    }
}