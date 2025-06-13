package com.example.deviceapi.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.deviceapi.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

/**
 * Global exception handler for the Device API.
 * <p>
 * This class centralizes the handling of exceptions thrown by controllers,
 * converting them into standardized and meaningful HTTP responses with proper
 * status codes and detailed error messages.
 * <p>
 * It improves API usability by providing consistent error structure, aiding
 * client-side error handling and debugging.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Handles ResourceNotFoundException (HTTP 404). Triggered when a requested
	 * resource is not found.
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
		return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), List.of(), request);
	}

	/**
	 * Handles IllegalOperationException (HTTP 409). Used when a business rule or
	 * domain constraint is violated.
	 */
	@ExceptionHandler(IllegalOperationException.class)
	public ResponseEntity<ErrorResponse> handleIllegalOperation(IllegalOperationException ex,
			HttpServletRequest request) {
		return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), List.of(), request);
	}

	/**
	 * Handles ConstraintViolationException (HTTP 400). Occurs when validation
	 * constraints on request parameters or path variables fail.
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
			HttpServletRequest request) {
		// Extract detailed messages from each violated constraint for client feedback
		List<String> details = ex.getConstraintViolations().stream()
				.map(v -> v.getPropertyPath() + ": " + v.getMessage()).collect(Collectors.toList());
		return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", details, request);
	}

	/**
	 * Handles MethodArgumentNotValidException (HTTP 400). Occurs when @Valid
	 * annotated request body fails validation.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		// Collect all field errors with messages to help client fix input
		List<String> details = ex.getBindingResult().getFieldErrors().stream()
				.map(field -> field.getField() + ": " + field.getDefaultMessage()).collect(Collectors.toList());
		return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", details, request);
	}

	/**
	 * Handles malformed JSON requests (HTTP 400). Occurs when JSON parsing fails
	 * due to syntax errors or invalid format.
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleJsonParseError(HttpMessageNotReadableException ex,
			HttpServletRequest request) {
		String detail = ex.getMostSpecificCause().getMessage();
		return buildErrorResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request", List.of(detail), request);
	}

	/**
	 * Handles missing request parameters (HTTP 400). Triggered when required query
	 * parameters are absent.
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex,
			HttpServletRequest request) {
		String detail = ex.getParameterName() + " parameter is missing";
		return buildErrorResponse(HttpStatus.BAD_REQUEST, "Missing required parameter", List.of(detail), request);
	}

	/**
	 * Handles unsupported HTTP methods (HTTP 405). Triggered when an HTTP method is
	 * not allowed on an endpoint.
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpServletRequest request) {
		String detail = "Method " + ex.getMethod() + " is not supported for this endpoint";
		return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed", List.of(detail), request);
	}

	/**
	 * Generic exception handler for all other unhandled exceptions (HTTP 500).
	 * Prevents exposing internal errors directly and provides a consistent error
	 * format.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred",
				List.of(ex.getMessage()), request);
	}

	/**
	 * Utility method to build a consistent error response structure.
	 *
	 * @param status  HTTP status to return
	 * @param message Summary error message
	 * @param details Detailed error information, e.g. validation errors
	 * @param request HttpServletRequest for URI info
	 * @return ResponseEntity containing ErrorResponse with status code
	 */
	private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, List<String> details,
			HttpServletRequest request) {
		ErrorResponse error = new ErrorResponse(LocalDateTime.now(), // Timestamp for error occurrence
				status.value(), // HTTP status code
				request.getRequestURI(), // Endpoint path where error occurred
				message, // Summary message
				details // Detailed error info (optional)
		);
		return ResponseEntity.status(status).body(error);
	}
}
