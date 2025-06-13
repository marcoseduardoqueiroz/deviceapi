package com.example.deviceapi.model;

/**
 * Enumeration representing the possible states of a Device.
 * 
 * Using an enum para representar estados do dispositivo melhora a legibilidade,
 * segurança de tipo (type safety) e evita valores mágicos espalhados pelo
 * código.
 */
public enum DeviceState {

	/**
	 * Indicates that the device is available for use.
	 */
	AVAILABLE,

	/**
	 * Indicates that the device is currently in use. Typically, operations like
	 * update or delete may be restricted when in this state.
	 */
	IN_USE,

	/**
	 * Indicates that the device is inactive and not currently available or in use.
	 */
	INACTIVE;
}
