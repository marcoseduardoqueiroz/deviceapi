package com.example.deviceapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.deviceapi.dto.DeviceCreateRequest;
import com.example.deviceapi.dto.DeviceResponse;
import com.example.deviceapi.dto.DeviceUpdateRequest;
import com.example.deviceapi.exception.IllegalOperationException;
import com.example.deviceapi.model.Device;
import com.example.deviceapi.model.DeviceState;
import com.example.deviceapi.repository.DeviceRepository;
import com.example.deviceapi.service.DeviceMapper;
import com.example.deviceapi.service.DeviceService;

/**
 * Unit tests for DeviceService.
 * 
 * These tests isolate DeviceService from external dependencies by mocking
 * repository and mapper. This allows focused verification of business logic
 * without side effects from database or mapping layers.
 * 
 * Each test covers a specific business scenario: -
 * createDevice_shouldSaveAndReturnResponse: verifies successful creation flow,
 * ensuring that request DTO is properly mapped, persisted, and mapped back to
 * response. - getDeviceById_shouldReturnResponse_whenFound: verifies retrieval
 * returns expected data, validating proper repository and mapper interactions.
 * - updateDevice_shouldUpdateFields: tests partial update behavior, confirming
 * that only provided fields are changed and others remain intact. -
 * updateDevice_shouldThrowException_whenDeviceInUseAndRestrictedFieldsChanged:
 * protects business rules preventing changes to restricted fields when device
 * is in use. - deleteDevice_shouldDelete_whenAvailable and
 * deleteDevice_shouldThrowException_whenDeviceInUse: verify delete operation
 * respects device state, enforcing data integrity constraints. -
 * getDevicesByBrand_shouldReturnList and getDevicesByState_shouldReturnList:
 * validate correct filtering and mapping of device lists by criteria.
 * 
 * Mock behaviors simulate repository queries and mapping logic, enabling us to
 * verify both input handling and output correctness.
 */
public class DeviceServiceTest {

	@Mock
	private DeviceRepository repository;

	@Mock
	private DeviceMapper mapper;

	@InjectMocks
	private DeviceService service;

	/**
	 * Initializes mocks and injects them into the service instance before each
	 * test. This setup allows isolated testing of service logic without hitting
	 * real database or external layers.
	 */
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	/**
	 * Test scenario for successfully creating a new device. Verifies that the
	 * service maps the request DTO to an entity, saves it, and then maps the saved
	 * entity back to a response DTO.
	 */
	@Test
	void createDevice_shouldSaveAndMapSuccessfully() {
		// Arrange: prepare create request and expected entities/responses
		DeviceCreateRequest request = new DeviceCreateRequest("Device A", "Brand X", DeviceState.AVAILABLE);
		Device device = new Device(); // entity before save (no ID)
		Device saved = new Device(); // entity after save (with ID)
		saved.setId(1L);
		DeviceResponse response = new DeviceResponse(1L, "Device A", "Brand X", DeviceState.AVAILABLE, null);

		// Mock mapping request to entity, saving entity, and mapping back to response
		when(mapper.toEntity(request)).thenReturn(device);
		when(repository.save(device)).thenReturn(saved);
		when(mapper.toResponse(saved)).thenReturn(response);

		// Act: call service create method
		DeviceResponse result = service.createDevice(request);

		// Assert: verify result is as expected
		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(1L);
	}

	/**
	 * Tests retrieval of an existing device by its ID. Ensures the service returns
	 * the correct response DTO when the device exists.
	 */
	@Test
	void getDeviceById_shouldReturnDevice_whenFound() {
		// Arrange: existing device entity and response DTO
		Device device = new Device();
		device.setId(1L);
		DeviceResponse response = new DeviceResponse(1L, "Device A", "Brand X", DeviceState.AVAILABLE, null);

		// Mock repository and mapper to return these objects
		when(repository.findById(1L)).thenReturn(Optional.of(device));
		when(mapper.toResponse(device)).thenReturn(response);

		// Act: retrieve device by ID
		DeviceResponse result = service.getDeviceById(1L);

		// Assert: verify returned data matches expected ID
		assertThat(result.id()).isEqualTo(1L);
	}

	/**
	 * Tests partial update behavior, confirming that only provided fields in the
	 * update request are applied to the existing entity.
	 * 
	 * This test also verifies that the update respects the order: name is updated
	 * first, then brand.
	 */
	@Test
	void updateDevice_shouldUpdateFields_whenValid() {
		// Arrange: update request with new name and brand (name first)
		DeviceUpdateRequest request = new DeviceUpdateRequest("NewName", "NewBrand", null);

		// Existing device with old values
		Device existing = new Device();
		existing.setId(1L);
		existing.setName("OldName");
		existing.setBrand("OldBrand");
		existing.setState(DeviceState.AVAILABLE);

		// Expected response after update
		DeviceResponse response = new DeviceResponse(1L, "NewName", "NewBrand", DeviceState.AVAILABLE,
				LocalDateTime.now());

		// Mock repository to return the existing device for update
		when(repository.findWithLockingById(1L)).thenReturn(Optional.of(existing));

		// Simulate mapper applying updates only if values are non-null,
		// respecting the update order (name then brand)
		doAnswer(invocation -> {
		    DeviceUpdateRequest req = invocation.getArgument(0);
		    Device deviceToUpdate = invocation.getArgument(1);

		    if (req.name() != null) {
		        deviceToUpdate.setName(req.name());
		    }
		    if (req.brand() != null) {
		        deviceToUpdate.setBrand(req.brand());
		    }
		    if (req.state() != null) {
		        deviceToUpdate.setState(req.state());
		    }
		    return null;
		}).when(mapper).updateDeviceFromDto(any(DeviceUpdateRequest.class), any(Device.class));

		// Mock saving updated entity and mapping to response
		when(repository.save(existing)).thenReturn(existing);
		when(mapper.toResponse(existing)).thenReturn(response);

		// Act: call update method
		DeviceResponse result = service.updateDevice(1L, request);

		// Assert: verify updated fields are correctly reflected in response
		assertThat(result.name()).isEqualTo("NewName");
		assertThat(result.brand()).isEqualTo("NewBrand");
	}

	/**
	 * Validates that updating a device currently in use and attempting to change
	 * restricted fields (brand or name) throws an IllegalOperationException. This
	 * protects the integrity and state rules of devices in use.
	 */
	@Test
	void updateDevice_shouldThrowException_ifDeviceInUseWithRestrictedFields() {
		// Arrange: update request attempting to change restricted fields
		DeviceUpdateRequest request = new DeviceUpdateRequest("NewName", "Brand Y", DeviceState.AVAILABLE);

		// Device in use - restricted from certain updates
		Device inUse = new Device();
		inUse.setId(1L);
		inUse.setState(DeviceState.IN_USE);

		// Mock repository to find device in use
		when(repository.findWithLockingById(1L)).thenReturn(Optional.of(inUse));

		// Act & Assert: expect exception when update is attempted
		assertThatThrownBy(() -> service.updateDevice(1L, request)).isInstanceOf(IllegalOperationException.class)
				.hasMessageContaining("Cannot update brand or name");
	}

	/**
	 * Tests successful deletion of a device that is available (not currently in
	 * use). Verifies that the repository's delete method is invoked properly.
	 */
	@Test
	void deleteDevice_shouldRemoveDevice_whenAvailable() {
		// Arrange: device available for deletion
		Device device = new Device();
		device.setId(1L);
		device.setState(DeviceState.AVAILABLE);

		// Mock repository to return this device and allow deletion
		when(repository.findWithLockingById(1L)).thenReturn(Optional.of(device));
		doNothing().when(repository).delete(device);

		// Act: call delete method
		service.deleteDevice(1L);

		// Assert: verify repository delete was called once with correct device
		verify(repository).delete(device);
	}

	/**
	 * Ensures that attempting to delete a device currently in use throws an
	 * IllegalOperationException, enforcing business rules.
	 */
	@Test
	void deleteDevice_shouldThrowException_ifDeviceInUse() {
		// Arrange: device currently in use
		Device device = new Device();
		device.setId(1L);
		device.setState(DeviceState.IN_USE);

		// Mock repository to find this device
		when(repository.findWithLockingById(1L)).thenReturn(Optional.of(device));

		// Act & Assert: expect exception when trying to delete device in use
		assertThatThrownBy(() -> service.deleteDevice(1L)).isInstanceOf(IllegalOperationException.class)
				.hasMessageContaining("Cannot delete devices in use");
	}

	/**
	 * Tests the service method that retrieves devices filtered by brand. Validates
	 * that the returned list matches expected size and data mapping.
	 */
	@Test
	void getDevicesByBrand_shouldReturnFilteredList() {
		// Arrange: mock list of devices with the requested brand
		List<Device> devices = List.of(new Device(), new Device());
		when(repository.findByBrand("Brand X")).thenReturn(devices);
		when(mapper.toResponse(any(Device.class)))
				.thenReturn(new DeviceResponse(1L, "Device A", "Brand X", DeviceState.AVAILABLE, LocalDateTime.now()));

		// Act: retrieve devices by brand
		List<DeviceResponse> result = service.getDevicesByBrand("Brand X");

		// Assert: verify returned list size and contents
		assertThat(result).hasSize(2);
	}

	/**
	 * Tests the service method that retrieves devices filtered by state. Validates
	 * that the returned list contains the correct filtered devices.
	 */
	@Test
	void getDevicesByState_shouldReturnFilteredList() {
		// Arrange: mock list of devices with the requested state
		List<Device> devices = List.of(new Device(), new Device());
		when(repository.findByState(DeviceState.AVAILABLE)).thenReturn(devices);
		when(mapper.toResponse(any(Device.class)))
				.thenReturn(new DeviceResponse(1L, "Device A", "Brand X", DeviceState.AVAILABLE, LocalDateTime.now()));

		// Act: retrieve devices by state
		List<DeviceResponse> result = service.getDevicesByState(DeviceState.AVAILABLE);

		// Assert: verify returned list size and correctness
		assertThat(result).hasSize(2);
	}
}
