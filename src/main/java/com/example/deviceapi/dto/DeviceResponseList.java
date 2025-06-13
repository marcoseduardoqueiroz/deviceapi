package com.example.deviceapi.dto;

import java.util.List;

/**
 * DTO representing the response payload for a list of Device API operations.
 * 
 * This immutable record encapsulates a list of device details returned to the
 * client after bulk retrieval operations.
 * 
 * Fields include:
 * - devices: a list of DeviceResponse records, each representing a single device's core details
 * 
 * Using a record ensures immutability and thread safety, preventing accidental
 * modification of response data.
 */
public record DeviceResponseList(List<DeviceResponse> devices) {
}
