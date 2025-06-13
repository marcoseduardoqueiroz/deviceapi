package com.example.deviceapi.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard structure for error responses returned by the API.
 * <p>
 * This DTO encapsulates comprehensive error details that help API clients
 * understand what went wrong and potentially how to fix it. It promotes
 * consistency in error reporting across different endpoints and exception
 * types.
 */
public record ErrorResponse(

		/**
		 * Timestamp indicating when the error occurred. Useful for tracing and
		 * correlating logs and client issues.
		 */
		LocalDateTime timestamp,

		/**
		 * HTTP status code associated with the error. Allows clients to
		 * programmatically react to different error types.
		 */
		int status,

		/**
		 * The URI path of the request that triggered the error. Helps identify which
		 * endpoint or resource caused the problem.
		 */
		String path,

		/**
		 * A concise, human-readable summary message describing the error. Intended to
		 * give a quick understanding of the failure.
		 */
		String message,

		/**
		 * Optional detailed information about the error. Often used to provide
		 * validation errors or stack traces in a controlled manner.
		 */
		List<String> details

) {
}
