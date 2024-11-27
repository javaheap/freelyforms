package com.utbm.da50.freelyform.exceptions;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {
  /**
   * Constructs a new ResourceNotFoundException with the specified detail message.
   *
   * @param message the detail message, which is saved for later retrieval
   *                by the {@link Throwable#getMessage()} method
   */
  public ResourceNotFoundException(String message) {
    super(message);
  }
}