CREATE TABLE users (
        login VARCHAR(50) PRIMARY KEY,
        isConnected BOOLEAN DEFAULT FALSE
);