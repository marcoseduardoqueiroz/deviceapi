package com.example.deviceapi.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.deviceapi.dto.DeviceCreateRequest;
import com.example.deviceapi.dto.DeviceResponse;
import com.example.deviceapi.dto.DeviceResponseList;
import com.example.deviceapi.dto.DeviceUpdateRequest;
import com.example.deviceapi.model.DeviceState;
import com.example.deviceapi.service.DeviceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller responsible for handling all device-related HTTP requests.
 * <p>
 * This controller exposes CRUD operations for Device entities, including search
 * by brand and state.
 * 
 * The endpoints are versioned under /api/v1/devices to allow for future API
 * evolution without breaking existing clients.
 * 
 * Validation is applied on incoming requests to ensure data integrity.
 * 
 * Logging is used to trace requests and responses for observability.
 */
@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
@Validated
@Tag(name = "Device Management", description = "Device API operations")
@Slf4j
public class DeviceController {

	private final DeviceService deviceService;

	/**
	 * Creates a new device resource.
	 * 
	 * @param request The device creation request DTO, validated for correctness.
	 * @return ResponseEntity containing the created device response with HTTP 201
	 *         status and Location header.
	 */
	@PostMapping
	@Operation(summary = "Create a new device")
	public ResponseEntity<DeviceResponse> createDevice(@Valid @RequestBody DeviceCreateRequest request) {

		log.info("POST /api/v1/devices - createDevice called with payload: name='{}', brand='{}', state='{}'",
				request.name(), request.brand(), request.state());

		DeviceResponse response = deviceService.createDevice(request);

		log.info("Device created successfully with id: {}", response.id());

		// Return HTTP 201 Created with the URI of the new resource
		return ResponseEntity.created(URI.create("/api/v1/devices/" + response.id())).body(response);
	}

	/**
	 * Retrieves a device by its unique identifier.
	 * 
	 * @param id The device ID to fetch.
	 * @return The device response DTO.
	 */
	@GetMapping("/{id}")
	@Operation(summary = "Get device by ID")
	public DeviceResponse getDeviceById(@PathVariable Long id) {
		log.info("GET /api/v1/devices/{} - getDeviceById called", id);
		return deviceService.getDeviceById(id);
	}

	/**
	 * Retrieves all devices.
	 * 
	 * @return List of all device response DTOs.
	 */
	@GetMapping
	@Operation(summary = "Get all devices")
	public DeviceResponseList getAllDevices() {
		log.info("GET /api/v1/devices - getAllDevices called");
		List<DeviceResponse> devices = deviceService.getAllDevices();
		return new DeviceResponseList(devices);
	}

	/**
	 * Updates an existing device fully.
	 * 
	 * Validations and domain rules are applied in the service layer, including
	 * restrictions on updating name/brand if device is in use.
	 * 
	 * @param id      The device ID to update.
	 * @param request The device update request DTO.
	 * @return The updated device response DTO.
	 */
	@PutMapping("/{id}")
	@Operation(summary = "Update a device (full update)")
	public DeviceResponse updateDevice(@PathVariable Long id, @Valid @RequestBody DeviceUpdateRequest request) {

		log.info("PUT /api/v1/devices/{} - updateDevice called with payload: {}", id, request);
		return deviceService.updateDevice(id, request);
	}

	/**
	 * Deletes a device by ID.
	 * 
	 * In-use devices cannot be deleted, this constraint is enforced by the service
	 * layer. Returns HTTP 204 No Content on successful deletion.
	 * 
	 * @param id The device ID to delete.
	 */
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Delete a device")
	public void deleteDevice(@PathVariable Long id) {
		log.info("DELETE /api/v1/devices/{} - deleteDevice called", id);
		deviceService.deleteDevice(id);
	}

	/**
	 * Searches devices by their brand.
	 * 
	 * @param brand The brand to filter devices.
	 * @return List of devices matching the brand.
	 */
	@GetMapping("/search/by-brand")
	@Operation(summary = "Search devices by brand")
	public List<DeviceResponse> getDevicesByBrand(@RequestParam String brand) {
		log.info("GET /api/v1/devices/search/by-brand - getDevicesByBrand called with brand='{}'", brand);
		return deviceService.getDevicesByBrand(brand);
	}

	/**
	 * Searches devices by their state.
	 * 
	 * @param state The state to filter devices (AVAILABLE, IN_USE, INACTIVE).
	 * @return List of devices matching the state.
	 */
	@GetMapping("/search/by-state")
	@Operation(summary = "Search devices by state")
	public List<DeviceResponse> getDevicesByState(@RequestParam DeviceState state) {
		log.info("GET /api/v1/devices/search/by-state - getDevicesByState called with state='{}'", state);
		return deviceService.getDevicesByState(state);
	}
}
