package com.example.deviceapi.exception;

/**
 * Exception thrown when a requested resource is not found in the system.
 * <p>
 * This is a custom unchecked exception used to signal HTTP 404 Not Found conditions
 * within the application domain, typically handled globally to provide
 * standardized error responses to clients.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message Detailed message explaining the cause of the exception.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
