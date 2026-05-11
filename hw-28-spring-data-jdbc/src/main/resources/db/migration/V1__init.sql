CREATE TABLE IF NOT EXISTS client (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS address (
    client_id BIGINT PRIMARY KEY,
    street VARCHAR(255),
    FOREIGN KEY (client_id) REFERENCES client(id)
);

CREATE TABLE IF NOT EXISTS phone (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    number VARCHAR(50),
    FOREIGN KEY (client_id) REFERENCES client(id)
);
