package com.example.deviceapi.service;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.example.deviceapi.dto.DeviceCreateRequest;
import com.example.deviceapi.dto.DeviceResponse;
import com.example.deviceapi.dto.DeviceUpdateRequest;
import com.example.deviceapi.model.Device;

/**
 * Mapper interface responsible for converting between Device entity and its
 * related DTOs. Utilizes MapStruct to generate type-safe and performant mapping
 * implementations.
 * 
 * Key advantages: - Clear separation between API layer (DTOs) and persistence
 * layer (entities). - Reduces boilerplate code by automating repetitive mapping
 * logic. - Supports partial updates with null-value ignoring to prevent
 * unintended overwrites.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeviceMapper {

	/**
	 * Maps DeviceCreateRequest DTO to Device entity.
	 * 
	 * Explicitly maps all fields to ensure fields are not silently omitted,
	 * enabling precise control over data transformation during creation.
	 * 
	 * @param dto the DeviceCreateRequest DTO containing input data
	 * @return a new Device entity populated from the DTO
	 */
	@org.mapstruct.Mapping(target = "name", source = "name")
	@org.mapstruct.Mapping(target = "brand", source = "brand")
	@org.mapstruct.Mapping(target = "state", source = "state")
	Device toEntity(DeviceCreateRequest dto);

	/**
	 * Converts a Device entity to DeviceResponse DTO.
	 * 
	 * This supports data encapsulation and abstraction when returning device data
	 * via APIs, exposing only necessary fields to clients.
	 * 
	 * @param entity the Device entity instance
	 * @return a DeviceResponse DTO representing the device data for API consumers
	 */
	DeviceResponse toResponse(Device entity);

	/**
	 * Updates an existing Device entity with values from a DeviceUpdateRequest DTO.
	 * 
	 * Uses @BeanMapping with NullValuePropertyMappingStrategy.IGNORE to avoid
	 * overwriting entity properties with null values from the DTO, supporting
	 * partial updates (PATCH semantics).
	 * 
	 * @param dto    the DeviceUpdateRequest DTO containing updated fields
	 * @param entity the existing Device entity to be updated (target)
	 */
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateDeviceFromDto(DeviceUpdateRequest dto, @MappingTarget Device entity);
}
