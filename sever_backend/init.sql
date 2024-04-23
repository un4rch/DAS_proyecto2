CREATE DATABASE IF NOT EXISTS calistenigram;

USE calistenigram;

CREATE TABLE Users (
    email VARCHAR(255) PRIMARY KEY,
    token VARCHAR(1024)
);

CREATE TABLE Images (
    direccion VARCHAR(255) PRIMARY KEY,
    email VARCHAR(255),
    likes INT,
    FOREIGN KEY (email) REFERENCES Users(email)
);

CREATE TABLE Likes (
    email VARCHAR(255),
    direccion VARCHAR(255),
    PRIMARY KEY (email, direccion),
    FOREIGN KEY (email) REFERENCES Users(email),
    FOREIGN KEY (direccion) REFERENCES Images(direccion)
);
