package com.example.deviceapi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.deviceapi.dto.DeviceCreateRequest;
import com.example.deviceapi.dto.DeviceResponse;
import com.example.deviceapi.dto.DeviceUpdateRequest;
import com.example.deviceapi.exception.IllegalOperationException;
import com.example.deviceapi.exception.ResourceNotFoundException;
import com.example.deviceapi.model.Device;
import com.example.deviceapi.model.DeviceState;
import com.example.deviceapi.repository.DeviceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service layer for managing Device entities.
 *
 * Applies business rules and validation to ensure data integrity and enforce
 * domain-specific constraints before persisting to the database.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

	private final DeviceRepository repository;
	private final DeviceMapper mapper;

	/**
	 * Creates a new Device from the request DTO. Automatically maps fields and
	 * persists to the database.
	 */
	@Transactional
	public DeviceResponse createDevice(DeviceCreateRequest request) {
		log.info("Creating new device: name={}, brand={}, state={}", request.name(), request.brand(), request.state());
		Device device = mapper.toEntity(request);
		Device saved = repository.save(device);
		log.debug("Device created with ID {}", saved.getId());
		return mapper.toResponse(saved);
	}

	/**
	 * Retrieves a single device by ID. Throws ResourceNotFoundException if not
	 * found.
	 */
	@Transactional(readOnly = true)
	public DeviceResponse getDeviceById(Long id) {
		log.info("Fetching device with ID {}", id);
		return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> {
			log.warn("Device with ID {} not found", id);
			return new ResourceNotFoundException("Device not found");
		});
	}

	/**
	 * Returns all devices in the database.
	 */
	@Transactional(readOnly = true)
	public List<DeviceResponse> getAllDevices() {
		log.info("Fetching all devices");
		return repository.findAll().stream().map(mapper::toResponse).collect(Collectors.toList());
	}

	/**
	 * Updates a device with given ID using values from the update request. Enforces
	 * immutability for devices currently in use.
	 */
	@Transactional
	public DeviceResponse updateDevice(Long id, DeviceUpdateRequest request) {
		log.info("Updating device with ID {}", id);
		Device device = repository.findWithLockingById(id).orElseThrow(() -> {
			log.warn("Device with ID {} not found for update", id);
			return new ResourceNotFoundException("Device not found");
		});

		// Enforce business rule: brand and name cannot be changed if device is in use
		if (device.isInUse()) {
			if (request.name() != null || request.brand() != null) {
				log.warn("Attempt to update name or brand of device in use - ID {}", id);
				throw new IllegalOperationException("Cannot update brand or name for devices in use");
			}
		}

		mapper.updateDeviceFromDto(request, device);
		Device updated = repository.save(device);
		log.debug("Device updated: ID={}, version={}", updated.getId(), updated.getVersion());
		return mapper.toResponse(updated);
	}

	/**
	 * Deletes a device by ID. Devices currently in use cannot be deleted.
	 */
	@Transactional
	public void deleteDevice(Long id) {
		log.info("Attempting to delete device with ID {}", id);
		Device device = repository.findWithLockingById(id).orElseThrow(() -> {
			log.warn("Device with ID {} not found for deletion", id);
			return new ResourceNotFoundException("Device not found");
		});

		if (device.isInUse()) {
			log.warn("Attempt to delete device in use - ID {}", id);
			throw new IllegalOperationException("Cannot delete devices in use");
		}

		repository.delete(device);
		log.info("Device with ID {} successfully deleted", id);
	}

	/**
	 * Fetches all devices matching a specific brand.
	 */
	@Transactional(readOnly = true)
	public List<DeviceResponse> getDevicesByBrand(String brand) {
		log.info("Fetching devices by brand: {}", brand);
		return repository.findByBrand(brand).stream().map(mapper::toResponse).collect(Collectors.toList());
	}

	/**
	 * Fetches all devices with a specific state.
	 */
	@Transactional(readOnly = true)
	public List<DeviceResponse> getDevicesByState(DeviceState state) {
		log.info("Fetching devices by state: {}", state);
		return repository.findByState(state).stream().map(mapper::toResponse).collect(Collectors.toList());
	}
}
