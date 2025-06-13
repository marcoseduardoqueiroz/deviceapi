package com.example.deviceapi.exception;

/**
 * Exception thrown when an illegal or invalid operation is attempted on a resource.
 * <p>
 * This custom unchecked exception is typically used to indicate business rule violations,
 * such as trying to modify or delete a resource in an invalid state.
 * It is usually mapped to HTTP 409 Conflict in REST APIs.
 */
public class IllegalOperationException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new IllegalOperationException with the specified detail message.
     *
     * @param message Detailed message explaining the reason for the exception.
     */
    public IllegalOperationException(String message) {
        super(message);
    }
}
