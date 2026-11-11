# AquaSafe – Existing Project Technical Analysis Report

**Document Version:** 1.0.0  
**Date:** November 2026  
**Project:** AquaSafe – Tamil Nadu Water-Borne Disease Prediction Using Big Data Analytics and Machine Learning  
**Analysis Author:** Antigravity Engineering Pair Programming System  

---

## 1. Executive Summary

A comprehensive architectural and code-level audit was conducted on the AquaSafe codebase located at `c:\Users\Admin\Documents\AquaSafe`. 

The existing application is a functional Spring Boot 3 + Weka + Web application that demonstrates baseline concepts of water-borne disease risk prediction. However, it operates on a limited scale (only 10 out of 38 Tamil Nadu districts, only 1 year of synthetic bi-weekly observations for 2025, a basic 8-table schema, and simulated Spark execution inside the Java service layer).

To evolve AquaSafe into a production-grade, enterprise Big Data Analytics and Machine Learning epidemiological platform, this document reviews the current architecture, catalogues existing modules and files, benchmarks existing datasets, identifies critical technical limitations, and outlines the precise architectural enhancements required to satisfy all master prompt specifications.

---

## 2. Existing Architecture

```
[Web Client Browser] (HTML5 / Bootstrap 5.3 / Chart.js / Leaflet.js)
         │  HTTP / REST / JSON
         ▼
[Spring Boot 3.2.5 Application Server (Java 17)]
    ├── Security Filter Chain (Spring Security, BCrypt, Session Tokens)
    ├── REST API Controllers (13 Controllers)
    ├── Business Service Layer (Spring @Service Beans)
    ├── ML Inference Layer (Weka 3.8.6 Random Forest, 100 Trees)
    └── Big Data Dispatcher (DataProcessingDispatcher)
         │                          │
         ▼                          ▼
    [Local Java Streams]     [PySpark Script / Local Cluster]
         │                          │
         ▼                          ▼
[Dual-Engine Persistence Layer]
    ├── Production Target: MySQL 8.x (localhost:3306)
    └── Fallback Target: Embedded In-Memory H2 (MySQL Mode)
```

### Key Architectural Characteristics:
- **Single Process Deployment:** The backend serves both REST API endpoints (`/api/...`) and static frontend web assets (`classpath:/static/`).
- **Dynamic Dual-Engine Database:** On startup, `DataSourceConfig` tests socket connectivity to MySQL on `localhost:3306`. If unavailable or authentication fails, it seamlessly falls back to an embedded in-memory H2 database running in MySQL compatibility mode.
- **Weka Serialization:** The ML model (`RandomForest-v1.0`) is built with Weka and serialized to `ml/models/random_forest_water_risk.model`.
- **Stateless & Session-based Dual Auth:** Spring Security is configured with HTTP Basic and form-based session tokens for `ADMIN` and `USER` roles.

---

## 3. Existing Modules & Codebase Inventory

### A. Backend Source Files (`backend/src/main/java/com/aquasafe/`)
- **Application Entry Point:**
  - `AquaSafeApplication.java`: Main Spring Boot application runner.
- **Configuration Layer:**
  - `config/DataSourceConfig.java`: Probes MySQL port 3306; activates embedded H2 fallback.
  - `config/DataInitializer.java`: Seeds default credentials (`admin`, `analyst`), imports baseline CSV files, trains Weka model, seeds 10 hardcoded prediction snapshots.
  - `config/FilePathResolver.java`: Resolves file paths across IDE and JAR execution directories.
  - `config/WebMvcConfig.java`: Configures static resource mappings and CORS.
- **Security Layer:**
  - `security/SecurityConfig.java`: Defines role-based authorization rules (`ROLE_ADMIN`, `ROLE_USER`).
  - `security/CustomUserDetailsService.java`: Loads user records from JPA repository.
- **Controllers (13 REST Controllers):**
  - `controller/SystemController.java`: `/api/system/health`, `/api/system/spark/process`.
  - `controller/AuthController.java`: `/api/auth/login`.
  - `controller/DashboardController.java`: Summary KPIs, disease trends, risk distribution, SSE live stream.
  - `controller/PredictionController.java`: `/api/predictions/predict`, `/api/predictions/history`.
  - `controller/ModelController.java`: Model evaluation metrics, confusion matrix, retraining trigger.
  - `controller/DatasetController.java`: Multipart CSV upload and import history.
  - `controller/PreprocessingController.java`: Automated cleaning and ARFF generation.
  - `controller/WaterQualityController.java`: Water quality observations pagination.
  - `controller/WeatherController.java`: Current weather via Open-Meteo and historical weather.
  - `controller/DiseaseController.java`: Disease records query endpoints.
  - `controller/PopulationController.java`: Demographic and coordinate retrieval.
  - `controller/AlertController.java`: Alert listing and status acknowledgment.
  - `controller/ReportController.java`: Filtered reports by district and date range.
- **Service Layer:**
  - `service/AlertService.java`: Evaluates high-risk conditions and saves alerts.
  - `service/DashboardService.java`: Aggregates summary KPIs and disease trends.
  - `service/DataPreprocessingService.java`: Deduplication, bounds checking, deterministic risk labeling, ARFF writing.
  - `service/DatasetImportService.java`: CSV ingestion using OpenCSV.
  - `service/DiseaseService.java`, `PopulationService.java`, `PredictionService.java`, `WaterQualityService.java`, `WeatherService.java`.
- **Big Data & Spark Layer:**
  - `spark/DataProcessingDispatcher.java`: Routes requests to Spark or Local processor.
  - `spark/DataProcessingService.java`: Common interface.
  - `spark/LocalDataProcessingService.java`: Multi-threaded Java streams aggregator.
  - `spark/SparkDataProcessingService.java`: Simulated Spark status stub.
- **Machine Learning Layer:**
  - `ml/WekaModelService.java`: Loads/trains Weka Random Forest classifier and runs 10-fold cross-validation.

### B. Big Data & External Scripts
- `spark/AquaSafeSparkJob.java`: Standalone Java Spark job (requires external Spark cluster / Hadoop).
- `spark/spark_processor.py`: PySpark script performing grouping and aggregations.
- `generate_demo_data.py`: Python generator producing synthetic 2025 records for 10 districts.

### C. Existing Frontend Files (`frontend/`)
- `index.html`: Redirector to dashboard.
- `dashboard.html`: Main dashboard with KPI cards, Chart.js trends, risk doughnut, recent alerts.
- `map.html`: Leaflet.js interactive map centered on Tamil Nadu with 10 district pins.
- `prediction.html`: Environmental parameter input form with preset buttons and prediction log.
- `data.html`: CSV file uploader and data preprocessing trigger.
- `model.html`: Model evaluation metrics display, 3x3 confusion matrix, retraining button.
- `alerts.html`: Surveillance advisory notices with read/unread toggle.
- `reports.html`: Tabular report viewer with print/PDF export styling.
- `login.html`: Login form with 1-click credential buttons.
- `css/style.css`: Custom responsive glassmorphic stylesheet.
- `js/`: Modular JavaScript (`api.js`, `dashboard.js`, `map.js`, `prediction.js`, `alerts.js`, `charts.js`).

---

## 4. Existing Database Structure

The current database structure consists of **8 tables**:

| Table Name | Primary Purpose | Fields / Columns |
| :--- | :--- | :--- |
| `users` | User credentials & roles | `id`, `username`, `password`, `role`, `created_at` |
| `population_data` | Demographic metrics (10 districts) | `id`, `district`, `state`, `population`, `population_density`, `sanitation_coverage`, `latitude`, `longitude`, `created_at` |
| `water_quality` | Water quality sensor readings | `id`, `district`, `state`, `date`, `ph`, `turbidity`, `tds`, `dissolved_oxygen`, `water_temperature`, `created_at` |
| `weather_data` | Meteorological readings | `id`, `district`, `state`, `date`, `temperature`, `rainfall`, `humidity`, `source`, `created_at` |
| `disease_records` | Clinical disease incidence | `id`, `district`, `state`, `date`, `cholera_cases`, `typhoid_cases`, `diarrhea_cases`, `hepatitis_cases`, `created_at` |
| `prediction_results` | ML prediction audit logs | `id`, `district`, `prediction_date`, `risk_level`, `probability`, `model_version`, environmental features |
| `alerts` | Public health warnings | `id`, `district`, `risk_level`, `probability`, `message`, `created_at`, `status` |
| `dataset_imports` | Ingestion audit log | `id`, `filename`, `dataset_type`, `total_records`, `valid_records`, `invalid_records`, `duplicate_records`, `missing_values`, `status`, `import_time` |

---

## 5. Existing Machine Learning Implementation

- **Library:** Weka 3.8.6 (`weka-stable`)
- **Algorithm:** Random Forest Classifier (`weka.classifiers.trees.RandomForest`)
- **Hyperparameters:**
  - Number of Iterations (Trees): 100
  - Random Seed: 42
  - Execution: In-memory evaluation and file serialization via `SerializationHelper`.
- **Feature Set (10 Features):**
  1. `district` (Nominal categorical factor - hardcoded to 10 districts)
  2. `ph` (Numeric: 0 to 14)
  3. `turbidity` (Numeric: NTU)
  4. `tds` (Numeric: mg/L)
  5. `dissolved_oxygen` (Numeric: mg/L)
  6. `temperature` (Numeric: °C)
  7. `rainfall` (Numeric: mm)
  8. `humidity` (Numeric: %)
  9. `previous_disease_cases` (Numeric)
  10. `population_density` (Numeric: persons/km²)
  11. `sanitation_coverage` (Numeric: %)
- **Target Label:** `risk_level` (Categorical: `LOW`, `MEDIUM`, `HIGH`)
- **Evaluation:** 10-fold cross-validation with accuracy, weighted precision, recall, F1-score, and 3x3 confusion matrix.

---

## 6. Existing Big Data Implementation

- **Java Spark Job (`spark/AquaSafeSparkJob.java`):**
  - Standalone class using Spark SQL 3.x Dataset API.
  - Reads CSVs from local filesystem or HDFS (`hdfs://localhost:9000/aquasafe/data/`).
  - Performs grouping and aggregations.
  - Writes Parquet output to `output/district_aggregates`.
  - **Limitation:** Not packaged into the Spring Boot backend JAR because Spark dependencies would create classpath conflicts with Spring Boot 3; invoked only via external spark-submit.
- **PySpark Job (`spark/spark_processor.py`):**
  - Standalone script using PySpark 3.x/4.x.
  - Reads `water_quality.csv`, `weather_data.csv`, `disease_records.csv`.
  - Performs Spark DataFrame transformations and aggregations.
- **Backend Dispatcher (`DataProcessingDispatcher.java`):**
  - Checks if Spark cluster is reachable.
  - Since local master is disabled by default in `SparkDataProcessingService.isAvailable()`, it automatically routes processing to `LocalDataProcessingService` using Java 8+ Streams.
  - **Limitation:** Returns hardcoded status and does not generate real persistent Parquet files or bridge to PySpark.

---

## 7. Existing Datasets

The existing datasets in `data/` were generated by `generate_demo_data.py`:
- `population_data.csv`: 10 rows (10 districts), static demographic attributes.
- `water_quality.csv`: 240 rows (10 districts × 24 bi-weekly dates from 2025-01-01 to 2025-12-15).
- `weather_data.csv`: 240 rows (same 10 districts × 24 bi-weekly dates).
- `disease_records.csv`: 240 rows (same 10 districts × 24 bi-weekly dates).
- `training_dataset.arff` / `training_dataset.csv`: 240 rows matching the above joins.

---

## 8. Identified Technical Issues & Limitations

1. **Incomplete Geographic Coverage:**
   - Only 10 districts are modeled (`Chennai, Madurai, Coimbatore, Tiruchirappalli, Salem, Tirunelveli, Thanjavur, Vellore, Erode, Kanyakumari`).
   - Tamil Nadu comprises **38 districts** (including Chengalpattu, Kallakurichi, Ranipet, Tenkasi, Tirupathur, Mayiladuthurai, etc.).
2. **Insufficient Historical Depth:**
   - Data spans only 1 year (2025), whereas the requirement demands **five complete years (2021–2025) plus current 2026 data**.
3. **Missing Parameters in Primary Datasets:**
   - Water Quality: Missing electrical conductivity, chloride, nitrate, fluoride, station name, observation type (groundwater lab vs live drinking sensor).
   - Weather: Missing minimum, maximum, average temperatures, wind speed, timestamps, source attribution.
   - Population: Missing urban/rural breakdown, official Census year attribution, and documented estimation formulas.
   - Disease: Missing disease-specific case definitions, suspect vs confirmed counts, reporting periods, data source provenance.
4. **Hardcoded Prediction Snapshots:**
   - `DataInitializer.seedInitialPredictions()` populates hardcoded prediction rows instead of running real inference.
5. **Spark Processing Not End-to-End Integrated:**
   - The REST API endpoint `/api/system/spark/process` defaults to local stream processing and does not execute real PySpark or produce real Parquet storage.
6. **Missing Schema Tables:**
   - Existing schema contains only 8 tables. The prompt mandates **17 normalized tables** including `districts`, `water_quality_records`, `weather_records`, `population_records`, `disease_records`, `dataset_sources`, `dataset_import_history`, `data_quality_reports`, `processed_analytics`, `ml_models`, `model_training_history`, `prediction_results`, `disease_forecasts`, `surveillance_alerts`, `alert_history`, `spark_job_history`.
7. **Missing ML Capabilities:**
   - No disease-specific prediction models (Cholera, Typhoid, Diarrhea, Hepatitis).
   - No time-series forecasting models (7-day and 30-day horizons with MAE/RMSE/MAPE).
   - No model comparison framework between Random Forest, Gradient Boosting, and XGBoost.
8. **Missing Frontend Modules:**
   - Missing dedicated UI views for District-Wise Intelligence (38 districts), Water Quality Analytics, Weather Analytics, Disease Surveillance, Disease Forecasting, Big Data Processing, and User Management.

---

## 9. Proposed Modifications & Architecture Enhancements

To address all requirements while maintaining the existing single-command runnability:

1. **Centralized District Master (All 38 Tamil Nadu Districts):**
   - Create comprehensive master data for all 38 districts with verified coordinates, 2011 Census populations, projected 2021–2026 populations, area, density, and sanitation indicators.
2. **Five-Year Multi-Source Data Collection & Generation Pipeline (2021–2026):**
   - Implement data collection and structured ingestion for 2021–2025 historical data and 2026 current data across all 38 districts.
   - Collect live weather data via Open-Meteo REST API.
   - Implement structured government data provenance tracking for CGWB, IMD, Census, and IDSP/IHIP datasets, clearly flagging real observations, estimations, and sample demonstration data.
3. **Database Architecture Expansion (17 Normalized Relational Tables):**
   - Upgrade schema to the required 17 tables in both MySQL 8.x and H2 MySQL mode.
   - Create corresponding JPA entities, repositories, and migration scripts.
4. **Real Big Data & PySpark Processing Pipeline:**
   - Implement real PySpark execution pipeline generating actual Parquet files partitioned by district and year.
   - Connect backend to execute and log real Spark job metrics (input records, processed records, partitions, duration, file size, status).
5. **Enhanced Machine Learning Engine:**
   - Model 1: General Water-borne Disease Risk Classification (Random Forest, Gradient Boosting, XGBoost comparison).
   - Model 2: Disease-Specific Risk Estimation (Cholera, Typhoid, Diarrhea, Hepatitis).
   - Model 3: Time-Series Forecasting (7-Day and 30-Day moving-average / exponential smoothing forecasts with MAE and RMSE metrics).
   - Dynamic Weka model retraining and evaluation across all 38 districts.
6. **Complete Frontend Enhancement (14 Modules):**
   - Implement all 14 required modules with real API bindings, interactive controls, and charts.
7. **Comprehensive Technical Documentation:**
   - Author all 12 mandatory markdown documentation files as specified.
8. **Testing & Single-Command Launcher Updates:**
   - Implement comprehensive backend and data pipeline tests.
   - Update `run.bat`, `run.ps1`, `run.py`, `run.sh`, `Makefile` to support automated health checking, PySpark execution, and zero-downtime startup.

