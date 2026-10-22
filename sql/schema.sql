-- AquaSafe: Water-Borne Disease Prediction Using Big Data Analytics
-- Database Schema for MySQL 8.x / MariaDB

CREATE DATABASE IF NOT EXISTS aquasafe CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE aquasafe;

-- 1. Water Quality Table
CREATE TABLE IF NOT EXISTS water_quality (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    district VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL DEFAULT 'Tamil Nadu',
    date DATE NOT NULL,
    ph DECIMAL(4, 2) NOT NULL,
    turbidity DECIMAL(6, 2) NOT NULL,
    tds DECIMAL(7, 2) NOT NULL,
    dissolved_oxygen DECIMAL(5, 2) NOT NULL,
    water_temperature DECIMAL(5, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_wq_district (district),
    INDEX idx_wq_date (date),
    INDEX idx_wq_district_date (district, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Weather Data Table
CREATE TABLE IF NOT EXISTS weather_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    district VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL DEFAULT 'Tamil Nadu',
    date DATE NOT NULL,
    temperature DECIMAL(5, 2) NOT NULL,
    rainfall DECIMAL(6, 2) NOT NULL,
    humidity DECIMAL(5, 2) NOT NULL,
    source VARCHAR(50) DEFAULT 'STORED_DATA',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_weather_district (district),
    INDEX idx_weather_date (date),
    INDEX idx_weather_district_date (district, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Disease Records Table
CREATE TABLE IF NOT EXISTS disease_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    district VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL DEFAULT 'Tamil Nadu',
    date DATE NOT NULL,
    cholera_cases INT NOT NULL DEFAULT 0,
    typhoid_cases INT NOT NULL DEFAULT 0,
    diarrhea_cases INT NOT NULL DEFAULT 0,
    hepatitis_cases INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_disease_district (district),
    INDEX idx_disease_date (date),
    INDEX idx_disease_district_date (district, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Population & Demographics Table
CREATE TABLE IF NOT EXISTS population_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    district VARCHAR(100) NOT NULL UNIQUE,
    state VARCHAR(100) NOT NULL DEFAULT 'Tamil Nadu',
    population BIGINT NOT NULL,
    population_density INT NOT NULL,
    sanitation_coverage DECIMAL(5, 2) NOT NULL,
    latitude DECIMAL(9, 6) NOT NULL,
    longitude DECIMAL(9, 6) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_pop_district (district)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Prediction Results Table
CREATE TABLE IF NOT EXISTS prediction_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    district VARCHAR(100) NOT NULL,
    prediction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    risk_level VARCHAR(20) NOT NULL,
    probability DECIMAL(5, 4) NOT NULL,
    model_version VARCHAR(50) NOT NULL,
    ph DECIMAL(4, 2),
    turbidity DECIMAL(6, 2),
    tds DECIMAL(7, 2),
    dissolved_oxygen DECIMAL(5, 2),
    temperature DECIMAL(5, 2),
    rainfall DECIMAL(6, 2),
    humidity DECIMAL(5, 2),
    previous_disease_cases INT,
    population_density INT,
    sanitation_coverage DECIMAL(5, 2),
    INDEX idx_pred_district (district),
    INDEX idx_pred_date (prediction_date),
    INDEX idx_pred_risk (risk_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Alerts Table
CREATE TABLE IF NOT EXISTS alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    district VARCHAR(100) NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    probability DECIMAL(5, 4) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'UNREAD',
    INDEX idx_alerts_district (district),
    INDEX idx_alerts_status (status),
    INDEX idx_alerts_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_users_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. Dataset Import History Table
CREATE TABLE IF NOT EXISTS dataset_imports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    dataset_type VARCHAR(50) NOT NULL,
    total_records INT NOT NULL,
    valid_records INT NOT NULL,
    invalid_records INT NOT NULL,
    duplicate_records INT NOT NULL,
    missing_values INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    import_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Default Users (Password: admin123, user123 hashed via BCrypt)
INSERT INTO users (username, password, role) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'ADMIN'),
('analyst', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'USER')
ON DUPLICATE KEY UPDATE role=VALUES(role);
