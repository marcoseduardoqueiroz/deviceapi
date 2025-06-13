package com.example.deviceapi.dto;

import com.example.deviceapi.model.DeviceState;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating Device information.
 *
 * This record allows partial updates where fields can be null, except for
 * validation constraints on size if values are provided.
 *
 * Swagger annotations improve auto-generated documentation and help clients
 * understand which fields are editable and under what conditions.
 */
@Schema(description = "Request body for updating an existing device (partial updates allowed)")
public record DeviceUpdateRequest(

		/**
		 * Device name can be updated; must not exceed 100 characters if present.
		 */
		@Schema(description = "Updated name of the device (optional)", example = "Galaxy S23", maxLength = 100, nullable = true) @Size(max = 100, message = "Name must be at most 100 characters") String name,

		/**
		 * Brand name can be updated; must not exceed 50 characters if present.
		 */
		@Schema(description = "Updated brand of the device (optional)", example = "Samsung", maxLength = 50, nullable = true) @Size(max = 50, message = "Brand must be at most 50 characters") String brand,

		/**
		 * Device state can be updated to a new status. This field is optional and
		 * accepts valid enum values.
		 */
		@Schema(description = "Updated device state (optional)", example = "IN_USE", nullable = true) DeviceState state

) {
}
