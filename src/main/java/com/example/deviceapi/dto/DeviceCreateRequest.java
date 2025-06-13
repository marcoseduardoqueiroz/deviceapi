package com.example.deviceapi.dto;

import com.example.deviceapi.model.DeviceState;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO representing the payload for creating a new device.
 * 
 * This record encapsulates the necessary fields required to create a device
 * resource in the system, ensuring validation constraints are enforced before
 * processing.
 * 
 * Validation annotations ensure data integrity and provide clear feedback
 * messages for invalid inputs, following best practices for API robustness and
 * user experience.
 */
@Schema(description = "Request body for creating a new device")
public record DeviceCreateRequest(

		/**
		 * The name or model of the device. Must not be blank and limited to 100
		 * characters, providing enough space for descriptive device names.
		 */
		@Schema(description = "The name or model of the device", example = "iPhone 14", maxLength = 100, required = true) @NotBlank(message = "Name must not be blank") @Size(max = 100, message = "Name must be at most 100 characters") String name,

		/**
		 * The brand or manufacturer of the device. Must not be blank and limited to 50
		 * characters to avoid excessively long or invalid values.
		 */
		@Schema(description = "The device brand or manufacturer", example = "Apple", maxLength = 50, required = true) @NotBlank(message = "Brand must not be blank") @Size(max = 50, message = "Brand must be at most 50 characters") String brand,

		/**
		 * The initial state of the device. Cannot be null to guarantee that every
		 * device has a valid status from the moment of creation.
		 */
		@Schema(description = "The initial state of the device", example = "AVAILABLE", required = true) @NotNull(message = "State must not be null") DeviceState state

) {
}
