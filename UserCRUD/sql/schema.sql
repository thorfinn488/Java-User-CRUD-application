-- =====================================================
-- User Management Database Schema
-- Run this script in MySQL before starting the app
-- =====================================================

-- Create the database
CREATE DATABASE IF NOT EXISTS user_management;

USE user_management;

-- Create the users table
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Optional: sample seed data for quick testing
INSERT INTO users (name, email, password, phone, address) VALUES
('John Doe', 'john.doe@example.com', 'pass123', '9876543210', '123 Main Street, New York'),
('Jane Smith', 'jane.smith@example.com', 'pass456', '9123456780', '45 Park Avenue, Boston');
