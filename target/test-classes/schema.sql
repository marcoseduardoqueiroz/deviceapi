CREATE TABLE devices (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    brand VARCHAR(255),
    state VARCHAR(50),
    creation_time TIMESTAMP,
    version INT DEFAULT 0
);
