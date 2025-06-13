package com.example.deviceapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.deviceapi.model.Device;
import com.example.deviceapi.model.DeviceState;

import jakarta.persistence.LockModeType;

/**
 * Repository interface for Device entities, extending JpaRepository to provide
 * CRUD operations and custom queries.
 * 
 * Using Spring Data JPA allows leveraging predefined methods as well as
 * defining custom queries with JPQL for more specific data retrieval.
 */
public interface DeviceRepository extends JpaRepository<Device, Long> {

	/**
	 * Finds devices by their brand.
	 * 
	 * @param brand The brand name to filter devices.
	 * @return List of devices matching the given brand.
	 * 
	 *         This method uses a JPQL query for explicit control, although Spring
	 *         Data could derive this query automatically. Explicit queries are
	 *         preferred for clarity and maintainability.
	 */
	@Query("SELECT d FROM Device d WHERE d.brand = :brand")
	List<Device> findByBrand(@Param("brand") String brand);

	/**
	 * Finds devices by their current state (e.g., AVAILABLE, IN_USE, INACTIVE).
	 * 
	 * @param state The device state to filter.
	 * @return List of devices matching the given state.
	 * 
	 *         Filtering by state allows clients to query devices by lifecycle
	 *         status, which is useful for operational management and reporting.
	 */
	@Query("SELECT d FROM Device d WHERE d.state = :state")
	List<Device> findByState(@Param("state") DeviceState state);

	/**
	 * Finds a device by its ID applying optimistic locking.
	 * 
	 * @param id The unique identifier of the device.
	 * @return Optional containing the device if found.
	 * 
	 *         The @Lock annotation with LockModeType.OPTIMISTIC ensures that when
	 *         updating or deleting a device, concurrent modifications are handled
	 *         gracefully, preventing lost updates in concurrent environments. This
	 *         is a best practice for high-concurrency REST APIs managing stateful
	 *         resources.
	 */
	@Lock(LockModeType.OPTIMISTIC)
	Optional<Device> findWithLockingById(Long id);
}
