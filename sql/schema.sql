-- AquaSafe: Tamil Nadu Water-Borne Disease Prediction Using Big Data Analytics
-- Production Schema for MySQL 8.x and In-Memory H2 (MySQL Mode)
-- 17 Normalized Relational Tables

CREATE DATABASE IF NOT EXISTS aquasafe CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE aquasafe;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'USER',
    full_name VARCHAR(100),
    email VARCHAR(100),
    department VARCHAR(100),
    assigned_district VARCHAR(100) DEFAULT 'All Districts',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_users_username (username),
    INDEX idx_users_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Districts Master Table (All 38 Districts)
CREATE TABLE IF NOT EXISTS districts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    district_id INT NOT NULL UNIQUE,
    district_code VARCHAR(20) NOT NULL UNIQUE,
    district_name VARCHAR(100) NOT NULL UNIQUE,
    state VARCHAR(50) NOT NULL DEFAULT 'Tamil Nadu',
    latitude DECIMAL(9, 6) NOT NULL,
    longitude DECIMAL(9, 6) NOT NULL,
    area_sq_km DECIMAL(10, 2) NOT NULL,
    census_2011_population BIGINT NOT NULL,
    population_2025 BIGINT NOT NULL,
    population_density INT NOT NULL,
    urban_population_pct DECIMAL(5, 2) NOT NULL,
    rural_population_pct DECIMAL(5, 2) NOT NULL,
    sanitation_coverage DECIMAL(5, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_dist_name (district_name),
    INDEX idx_dist_code (district_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Water Quality Records Table
CREATE TABLE IF NOT EXISTS water_quality_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    district_id INT,
    district_code VARCHAR(20),
    district_name VARCHAR(100) NOT NULL,
    station_name VARCHAR(150),
    observation_date DATE NOT NULL,
    observation_type VARCHAR(50) DEFAULT 'GROUNDWATER_LAB',
    ph DECIMAL(5, 2) NOT NULL,
    turbidity DECIMAL(6, 2),
    tds DECIMAL(8, 2) NOT NULL,
    dissolved_oxygen DECIMAL(5, 2),
    electrical_conductivity DECIMAL(8, 2),
    chloride DECIMAL(7, 2),
    nitrate DECIMAL(6, 2),
    fluoride DECIMAL(5, 2),
    water_temperature DECIMAL(5, 2),
    turbidity_available TINYINT(1) DEFAULT 1,
    do_available TINYINT(1) DEFAULT 1,
    data_source VARCHAR(100) DEFAULT 'CENTRAL_GROUND_WATER_BOARD_TN',
    validation_status VARCHAR(50) DEFAULT 'VALIDATED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_wqr_dist (district_name),
    INDEX idx_wqr_date (observation_date),
    INDEX idx_wqr_dist_date (district_name, observation_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Compatible Alias View / Table for legacy queries
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

-- 4. Weather Records Table
CREATE TABLE IF NOT EXISTS weather_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    district_id INT,
    district_code VARCHAR(20),
    district_name VARCHAR(100) NOT NULL,
    observation_date DATE NOT NULL,
    temp_min DECIMAL(5, 2),
    temp_max DECIMAL(5, 2),
    temp_avg DECIMAL(5, 2) NOT NULL,
    rainfall DECIMAL(6, 2) NOT NULL,
    humidity DECIMAL(5, 2) NOT NULL,
    wind_speed DECIMAL(5, 2),
    source_attribution VARCHAR(100) DEFAULT 'IMD_HISTORICAL_GRIDDED',
    observation_timestamp VARCHAR(50),
    data_type VARCHAR(50) DEFAULT 'HISTORICAL_OBSERVED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_weather_dist (district_name),
    INDEX idx_weather_date (observation_date),
    INDEX idx_weather_dist_date (district_name, observation_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Compatible Legacy Table
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
    INDEX idx_wth_district (district),
    INDEX idx_wth_date (date),
    INDEX idx_wth_district_date (district, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Population Records Table
CREATE TABLE IF NOT EXISTS population_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    district_id INT,
    district_name VARCHAR(100) NOT NULL,
    district_code VARCHAR(20),
    year INT NOT NULL,
    total_population BIGINT NOT NULL,
    population_density INT NOT NULL,
    urban_population BIGINT,
    rural_population BIGINT,
    sanitation_coverage DECIMAL(5, 2) NOT NULL,
    source VARCHAR(100) DEFAULT 'CENSUS_INDIA_2011_PROJECTED',
    is_estimated TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_pop_dist_year (district_name, year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Compatible Legacy Table
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

-- 6. Disease Records Table
CREATE TABLE IF NOT EXISTS disease_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    district_id INT,
    district_code VARCHAR(20),
    district_name VARCHAR(100) NOT NULL,
    district VARCHAR(100),
    state VARCHAR(100) NOT NULL DEFAULT 'Tamil Nadu',
    date DATE NOT NULL,
    reporting_period VARCHAR(30) DEFAULT 'MONTHLY',
    population BIGINT,
    cholera_cases INT NOT NULL DEFAULT 0,
    typhoid_cases INT NOT NULL DEFAULT 0,
    diarrhea_cases INT NOT NULL DEFAULT 0,
    hepatitis_cases INT NOT NULL DEFAULT 0,
    total_cases INT NOT NULL DEFAULT 0,
    incidence_rate_per_100k DECIMAL(8, 2),
    data_source VARCHAR(100) DEFAULT 'IDSP_WEEKLY_SURVEILLANCE',
    reporting_status VARCHAR(50) DEFAULT 'CONFIRMED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_dis_dist (district_name),
    INDEX idx_dis_date (date),
    INDEX idx_dis_dist_date (district_name, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Dataset Sources Table (Provenance Registry)
CREATE TABLE IF NOT EXISTS dataset_sources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_id INT UNIQUE,
    source_name VARCHAR(150) NOT NULL,
    source_url VARCHAR(255) NOT NULL,
    dataset_category VARCHAR(50) NOT NULL,
    download_date DATE,
    observation_period VARCHAR(100),
    district_coverage VARCHAR(100),
    record_count INT DEFAULT 0,
    data_format VARCHAR(50),
    validation_status VARCHAR(50) DEFAULT 'VALIDATED',
    data_classification VARCHAR(100),
    access_restrictions TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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

-- 9. Data Quality Reports Table
CREATE TABLE IF NOT EXISTS data_quality_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dataset_name VARCHAR(100) NOT NULL,
    check_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_records INT NOT NULL,
    completeness_pct DECIMAL(5, 2) NOT NULL,
    duplicate_count INT NOT NULL DEFAULT 0,
    outlier_count INT NOT NULL DEFAULT 0,
    missing_values_count INT NOT NULL DEFAULT 0,
    validity_status VARCHAR(50) NOT NULL DEFAULT 'PASSED',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. Processed Analytics Table (Aggregated Analytics Cache)
CREATE TABLE IF NOT EXISTS processed_analytics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    district_name VARCHAR(100) NOT NULL,
    analysis_date DATE NOT NULL,
    avg_ph DECIMAL(5, 2),
    avg_turbidity DECIMAL(6, 2),
    avg_tds DECIMAL(8, 2),
    avg_do DECIMAL(5, 2),
    avg_temperature DECIMAL(5, 2),
    total_rainfall DECIMAL(7, 2),
    avg_humidity DECIMAL(5, 2),
    total_disease_cases INT,
    disease_incidence_rate DECIMAL(8, 2),
    calculated_wqi DECIMAL(6, 2),
    risk_classification VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_pa_dist_date (district_name, analysis_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. ML Models Table (Catalog & Active Weights)
CREATE TABLE IF NOT EXISTS ml_models (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL,
    model_type VARCHAR(50) NOT NULL,
    model_version VARCHAR(50) NOT NULL UNIQUE,
    algorithm VARCHAR(100) NOT NULL,
    target_variable VARCHAR(100) NOT NULL,
    training_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_training_samples INT NOT NULL,
    accuracy DECIMAL(5, 2) NOT NULL,
    precision_score DECIMAL(5, 2) NOT NULL,
    recall_score DECIMAL(5, 2) NOT NULL,
    f1_score DECIMAL(5, 2) NOT NULL,
    roc_auc DECIMAL(5, 4),
    active TINYINT(1) DEFAULT 1,
    file_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. Model Training History Table
CREATE TABLE IF NOT EXISTS model_training_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL,
    model_version VARCHAR(50) NOT NULL,
    training_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    dataset_name VARCHAR(100) NOT NULL,
    instances_count INT NOT NULL,
    folds_count INT NOT NULL DEFAULT 10,
    accuracy DECIMAL(5, 2) NOT NULL,
    f1_score DECIMAL(5, 2) NOT NULL,
    training_duration_ms BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'SUCCESS',
    summary TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 13. Prediction Results Table
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
    predicted_cholera_risk VARCHAR(20) DEFAULT 'LOW',
    predicted_typhoid_risk VARCHAR(20) DEFAULT 'LOW',
    predicted_diarrhea_risk VARCHAR(20) DEFAULT 'LOW',
    predicted_hepatitis_risk VARCHAR(20) DEFAULT 'LOW',
    advisory_message TEXT,
    INDEX idx_pred_district (district),
    INDEX idx_pred_date (prediction_date),
    INDEX idx_pred_risk (risk_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 14. Disease Forecasts Table (7-Day and 30-Day Projections)
CREATE TABLE IF NOT EXISTS disease_forecasts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    district VARCHAR(100) NOT NULL,
    disease_name VARCHAR(100) NOT NULL,
    forecast_date DATE NOT NULL,
    horizon_days INT NOT NULL,
    forecasted_cases INT NOT NULL,
    lower_confidence INT NOT NULL,
    upper_confidence INT NOT NULL,
    model_used VARCHAR(100) NOT NULL,
    mae DECIMAL(6, 2),
    rmse DECIMAL(6, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_fc_dist_disease (district, disease_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 15. Surveillance Alerts Table
CREATE TABLE IF NOT EXISTS surveillance_alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    district VARCHAR(100) NOT NULL,
    disease VARCHAR(100) DEFAULT 'General Water-Borne Risk',
    risk_level VARCHAR(20) NOT NULL,
    probability DECIMAL(5, 4) NOT NULL,
    message TEXT NOT NULL,
    triggering_factors TEXT,
    recommended_action TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'UNREAD',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_alerts_dist (district),
    INDEX idx_alerts_stat (status),
    INDEX idx_alerts_time (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Compatible Legacy Alerts Table
CREATE TABLE IF NOT EXISTS alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    district VARCHAR(100) NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    probability DECIMAL(5, 4) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'UNREAD',
    INDEX idx_alt_district (district),
    INDEX idx_alt_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 16. Alert History Table (Acknowledgment & Action Audit)
CREATE TABLE IF NOT EXISTS alert_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    alert_id BIGINT,
    district VARCHAR(100) NOT NULL,
    action_taken VARCHAR(100) NOT NULL,
    acknowledged_by VARCHAR(50) NOT NULL,
    action_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    comments TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 17. Spark Job History Table
CREATE TABLE IF NOT EXISTS spark_job_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_name VARCHAR(150) NOT NULL,
    spark_master VARCHAR(100) NOT NULL,
    input_records INT NOT NULL,
    processed_records INT NOT NULL,
    rejected_records INT NOT NULL DEFAULT 0,
    partitions_count INT NOT NULL,
    duration_ms BIGINT NOT NULL,
    dataset_size_bytes BIGINT NOT NULL,
    parquet_path VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    execution_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Default Users
INSERT INTO users (username, password, role, full_name, email, department) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'ADMIN', 'System Administrator', 'admin@aquasafe.gov.in', 'State Epidemiology Directorate'),
('analyst', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'USER', 'Public Health Analyst', 'analyst@aquasafe.gov.in', 'Surveillance Operations Unit'),
('dho', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'DHO', 'District Health Officer', 'dho@aquasafe.gov.in', 'District Health Surveillance')
ON DUPLICATE KEY UPDATE role=VALUES(role);
