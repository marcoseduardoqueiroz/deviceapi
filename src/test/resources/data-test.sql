-- Clean the table
DELETE FROM devices;

-- Insert device with id 1 (normal device, not in use)
INSERT INTO devices (id, name, brand, state, creation_time) VALUES
(1, 'Device 1', 'Brand A', 'AVAILABLE', CURRENT_TIMESTAMP);

-- Insert device with id 2 (device in use, to test business rule)
INSERT INTO devices (id, name, brand, state, creation_time) VALUES
(2, 'Device 2', 'Brand B', 'IN_USE', CURRENT_TIMESTAMP);
