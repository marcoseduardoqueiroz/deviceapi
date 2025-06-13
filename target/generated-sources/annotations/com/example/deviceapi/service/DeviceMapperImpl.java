package com.example.deviceapi.service;

import com.example.deviceapi.dto.DeviceCreateRequest;
import com.example.deviceapi.dto.DeviceResponse;
import com.example.deviceapi.dto.DeviceUpdateRequest;
import com.example.deviceapi.model.Device;
import com.example.deviceapi.model.DeviceState;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-12T22:06:39-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22.0.1 (Eclipse Adoptium)"
)
@Component
public class DeviceMapperImpl implements DeviceMapper {

    @Override
    public Device toEntity(DeviceCreateRequest dto) {
        if ( dto == null ) {
            return null;
        }

        Device device = new Device();

        device.setName( dto.name() );
        device.setBrand( dto.brand() );
        device.setState( dto.state() );

        return device;
    }

    @Override
    public DeviceResponse toResponse(Device entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        String brand = null;
        DeviceState state = null;
        LocalDateTime creationTime = null;

        id = entity.getId();
        name = entity.getName();
        brand = entity.getBrand();
        state = entity.getState();
        creationTime = entity.getCreationTime();

        DeviceResponse deviceResponse = new DeviceResponse( id, name, brand, state, creationTime );

        return deviceResponse;
    }

    @Override
    public void updateDeviceFromDto(DeviceUpdateRequest dto, Device entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.name() != null ) {
            entity.setName( dto.name() );
        }
        if ( dto.brand() != null ) {
            entity.setBrand( dto.brand() );
        }
        if ( dto.state() != null ) {
            entity.setState( dto.state() );
        }
    }
}
