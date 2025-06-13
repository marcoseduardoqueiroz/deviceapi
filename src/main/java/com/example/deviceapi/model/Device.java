package com.example.deviceapi.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity class representing a Device in the system.
 * 
 * This class is mapped to the 'devices' table in the database using JPA annotations.
 * It encapsulates device attributes, including identification, brand, state, creation timestamp, and versioning for concurrency control.
 */
@Entity
@Table(name = "devices")
@Getter
@Setter
public class Device {
    
    /**
     * Primary key, auto-generated using the IDENTITY strategy.
     * This ensures unique identification of each device entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Device name, limited to 100 characters and not nullable.
     * This field stores the user-friendly name or description of the device.
     */
    @Column(nullable = false, length = 100)
    private String name;
    
    /**
     * Device brand, limited to 50 characters and not nullable.
     * It represents the manufacturer or brand of the device.
     */
    @Column(nullable = false, length = 50)
    private String brand;
    
    /**
     * Device state stored as a string representing an enum value.
     * Using EnumType.STRING improves readability and maintainability in the database.
     * The default state is set to AVAILABLE.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeviceState state = DeviceState.AVAILABLE;
    
    /**
     * Timestamp of device creation, automatically set at insertion time.
     * The field is not updatable to preserve the original creation date.
     * Leveraging Hibernate's @CreationTimestamp simplifies audit tracking.
     */
    @CreationTimestamp
    @Column(name = "creation_time", nullable = false, updatable = false)
    private LocalDateTime creationTime; 
    
    /**
     * Version field for optimistic locking and concurrency control.
     * Ensures safe updates in concurrent environments by detecting conflicting changes.
     */
    @Version
    private Long version; 
    
    /**
     * Utility method to check if the device is currently in use.
     * This is a domain-driven design approach, encapsulating business logic in the entity.
     * 
     * @return true if device state is IN_USE, false otherwise
     */
    public boolean isInUse() {
        return state == DeviceState.IN_USE;
    }
}
