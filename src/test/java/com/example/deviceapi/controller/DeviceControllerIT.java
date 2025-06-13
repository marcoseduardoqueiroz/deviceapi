package com.example.deviceapi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.deviceapi.Application;

/**
 * Integration test class for DeviceController REST endpoints.
 * 
 * This class uses MockMvc to simulate real HTTP requests to the API, verifying
 * end-to-end behavior including request handling, validation, business rules
 * enforcement, and proper HTTP status and response content.
 * 
 * These tests are critical to ensure that the Device API endpoints behave
 * correctly under various scenarios, including both success and error cases, by
 * interacting with the full application context (controller, service,
 * repository).
 * 
 * Key points for defense: - Use of @SpringBootTest loads the entire application
 * context to test realistic flows. - @AutoConfigureMockMvc configures MockMvc
 * for HTTP request simulation without a real server. - Validation of JSON
 * responses ensures contract compliance with API clients. - Testing business
 * logic constraints such as preventing updates or deletions of devices in use.
 */
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DeviceControllerIT {

	@BeforeAll
	static void init() {
		System.out.println("Running tests with profile: " + System.getProperty("spring.profiles.active"));
	}

	@Autowired
	private MockMvc mockMvc;

	/**
	 * Tests successful update of a device's fields.
	 * 
	 * Sends a PUT request to update the device identified by ID 1, verifies that
	 * HTTP 200 OK is returned along with the expected updated values.
	 * 
	 * Demonstrates that valid update requests are correctly processed and response
	 * JSON contains updated fields.
	 */
	@Test
	void shouldUpdateDeviceSuccessfully() throws Exception {
		String updateJson = """
				{
				    "name": "Updated Device",
				    "brand": "Brand Updated"
				}
				""";

		mockMvc.perform(put("/api/v1/devices/{id}", 1).contentType(MediaType.APPLICATION_JSON).content(updateJson))
				.andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Updated Device"))
				.andExpect(jsonPath("$.brand").value("Brand Updated"));
	}

	/**
	 * Tests update attempt on a non-existent device.
	 * 
	 * Sends a PUT request for a device ID (9999) that does not exist, expecting an
	 * HTTP 404 Not Found response with an appropriate error message.
	 * 
	 * Validates the API's proper error handling when resource is missing.
	 */
	@Test
	void shouldReturn404WhenUpdatingNonExistentDevice() throws Exception {
		String updateJson = """
				{
				    "name": "DoesNotExist"
				}
				""";

		mockMvc.perform(put("/api/v1/devices/{id}", 9999).contentType(MediaType.APPLICATION_JSON).content(updateJson))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("Device not found"));
	}

	/**
	 * Tests business rule enforcement: prevents updating name or brand of a device
	 * currently in use.
	 * 
	 * Sends a PUT request for device with ID 2, assumed to be in IN_USE state in
	 * test database. Expects HTTP 400 Bad Request and error message indicating the
	 * update is forbidden.
	 * 
	 * This ensures the API correctly enforces domain-specific constraints during
	 * updates.
	 */
	@Test
	void shouldReturn409WhenUpdatingDeviceInUse() throws Exception {
		String updateJson = """
				{
				    "name": "NewName",
				    "brand": "NewBrand"
				}
				""";

		// Device with ID 2 expected to be IN_USE in test DB setup
		mockMvc.perform(put("/api/v1/devices/{id}", 2).contentType(MediaType.APPLICATION_JSON).content(updateJson))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.message").value("Cannot update brand or name for devices in use"));
	}

	/**
	 * Tests successful deletion of an existing device.
	 * 
	 * Sends a DELETE request for device ID 1, expects HTTP 204 No Content status
	 * indicating successful removal.
	 * 
	 * Validates normal delete operation flow.
	 */
	@Test
	void shouldDeleteDeviceSuccessfully() throws Exception {
		mockMvc.perform(delete("/api/v1/devices/{id}", 1)).andExpect(status().isNoContent());
	}

	/**
	 * Tests deletion attempt on a non-existent device.
	 * 
	 * Sends a DELETE request for a device ID (9999) not present in the database,
	 * expects HTTP 404 Not Found and an error message.
	 * 
	 * Validates proper handling of resource absence on delete operations.
	 */
	@Test
	void shouldReturn404WhenDeletingNonExistentDevice() throws Exception {
		mockMvc.perform(delete("/api/v1/devices/{id}", 9999)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Device not found"));
	}

	/**
	 * Tests business rule enforcement: prevents deletion of devices currently in
	 * use.
	 * 
	 * Sends a DELETE request for device ID 2, assumed IN_USE in test database.
	 * Expects HTTP 400 Bad Request and error message forbidding the operation.
	 * 
	 * Ensures critical domain constraints are upheld via the API layer.
	 */
	@Test
	void shouldReturn409WhenDeletingDeviceInUse() throws Exception {
		// Device with ID 2 expected to be IN_USE in test DB setup
		mockMvc.perform(delete("/api/v1/devices/{id}", 2)).andExpect(status().isConflict())
				.andExpect(jsonPath("$.message").value("Cannot delete devices in use"));
	}
}
