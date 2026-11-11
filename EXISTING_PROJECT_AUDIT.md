# AquaSafe: Existing Project Comprehensive Audit & Health Assessment

**Document Version:** 2.0  
**Audit Timestamp:** November 2026  
**Auditor:** Senior Full-Stack, Big Data & Machine Learning Systems Architect  
**Project:** AquaSafe – Tamil Nadu Water-Borne Disease Prediction Using Big Data Analytics and Machine Learning

---

## 1. Existing Project Architecture

AquaSafe is structured as a multi-tier, data-driven epidemiological surveillance platform:
- **Presentation Layer:** Static multi-page web application (HTML5, Bootstrap 5.3.3, Chart.js 4.4, Leaflet.js GIS, ES6 JavaScript) served by Spring Boot's embedded resource handler at `/` and mirrored in `frontend/` and `backend/src/main/resources/static/`.
- **Application & Service Layer:** Java 17 LTS / Spring Boot 3.2.5 REST API backend hosting 17 REST controllers, domain service classes, OpenCSV ingestion pipelines, Spring Data JPA repositories, and an SSE event streaming bus.
- **Big Data Analytics Layer:** Distributed Apache Spark 4.2.0 / PySpark processing engine with local multi-threaded fallback, Spark SQL transformations, window functions, and columnar Snappy-compressed Parquet storage.
- **Machine Learning Layer:** Hybrid dual-runtime engine:
  - Python Scikit-Learn 1.9.1 training pipeline with chronological train/test split (2021–2024 train, 2025–2026 test).
  - Weka 3.8.6 in-process Java Random Forest classifier loading serialized models and ARFF attribute datasets for sub-millisecond real-time REST prediction serving.
- **Persistence Layer:** Dual-database architecture using Spring Boot `DataSourceConfig`:
  - Production Engine: MySQL 8.x on port 3306 with HikariCP connection pool.
  - Fallback Engine: In-memory H2 Database running in MySQL compatibility mode (`MODE=MySQL;DB_CLOSE_DELAY=-1`).

---

## 2. Existing Technology Stack

| Layer | Component | Version | Role & Description |
|---|---|---|---|
| **Backend Framework** | Spring Boot | 3.2.5 | Enterprise REST controllers, dependency injection, service management |
| **Language Runtime** | Java OpenJDK | 17 LTS | Core backend programming language |
| **Security** | Spring Security | 6.2.x | BCrypt password hashing, session management, role-based security |
| **Big Data Processing** | Apache Spark / PySpark | 4.2.0 | Distributed DataFrame processing, multi-source joins, Parquet export |
| **Storage Engine** | Apache Parquet / PyArrow | 26.0.0 | Columnar Snappy-compressed binary storage |
| **Machine Learning** | Scikit-Learn / Weka | 1.9.1 / 3.8.6 | Random Forest, Gradient Boosting, Extra Trees, time-series forecasting |
| **Database (Prod / Fallback)**| MySQL / H2 | 8.0+ / 2.2.x | Relational persistence with 17 normalized tables and auto-switching |
| **CSV Processing** | OpenCSV | 5.9 | High-throughput streaming ingestion of multi-source CSV records |
| **Frontend Framework** | Bootstrap | 5.3.3 | Responsive grid, components, and layout styling |
| **Geospatial GIS** | Leaflet.js | 1.9.4 | Interactive OpenStreetMap visualization for all 38 districts |
| **Visual Analytics** | Chart.js | 4.4.1 | Time-series trends, disease proportions, and forecast bounds |
| **Typography & Icons** | FontAwesome / Inter | 6.5.1 / Google | Iconography and typography system |
| **External API** | Open-Meteo REST API | Current | Live meteorological data provider for Tamil Nadu coordinates |

---

## 3. All Frontend Modules (14 Modules)

| # | Module | HTML File | Script | Purpose & Status |
|---|---|---|---|---|
| 1 | **Dashboard** | `dashboard.html` | `js/dashboard.js`, `js/api.js` | Statewide overview, KPIs, risk distribution, live SSE stream |
| 2 | **District Intelligence** | `districts.html` | Embedded script, `js/api.js` | All 38 districts directory, census/CAGR populations, sanitation ranking |
| 3 | **Geospatial Risk Map** | `map.html` | `js/map.js`, Leaflet.js | GIS heatmap, disease filters, 38 district pins with popups |
| 4 | **Risk Prediction Engine** | `prediction.html` | `js/prediction.js`, `js/api.js` | Interactive ML risk evaluation, disease probabilities, WHO advisories |
| 5 | **Disease Forecasting** | `forecast.html` | Embedded script, Chart.js | 7-day and 30-day ahead projections with 95% confidence intervals |
| 6 | **Water Quality Analytics**| `water-quality.html`| Embedded script, `js/api.js` | BIS 10500:2012 standards, WAWQI scores, threshold violations |
| 7 | **Weather Analytics** | `weather.html` | Embedded script, `js/api.js` | IMD observations, Northeast/Southwest monsoon tracking, SMAs |
| 8 | **Disease Surveillance** | `disease.html` | Embedded script, Chart.js | Cholera, Typhoid, ADD, Hepatitis case logs, incidence rates table |
| 9 | **Big Data & Spark** | `spark.html` | Embedded script, `js/api.js` | Spark execution console, partition metrics, Parquet telemetry |
| 10 | **Data Management** | `data.html` | Embedded script, `js/api.js` | Dataset sources registry, CSV ingestion with validation, cleaning |
| 11 | **Model Performance** | `model.html` | Embedded script, `js/api.js` | Benchmarks (RF vs GB vs ET), 10-fold CV confusion matrix, importances |
| 12 | **Surveillance Alerts** | `alerts.html` | `js/alerts.js`, `js/api.js` | Real-time warnings (CRITICAL, WARNING, INFO) with advisories |
| 13 | **Analytical Reports** | `reports.html` | Embedded script, `js/api.js` | Filterable executive summaries with print and PDF export |
| 14 | **User Management** | `users.html` | Embedded script, `js/api.js` | RBAC user directory, registration modal, role management |

---

## 4. All Backend Services

1. **`DashboardService.java`**: Aggregates statewide KPIs, computes risk distributions, manages SSE emitters, and broadcasts live dashboard metrics.
2. **`DistrictService` / `DistrictRepository`**: Centralized master access for all 38 districts of Tamil Nadu.
3. **`WaterQualityService.java`**: Computes Weighted Arithmetic Water Quality Index (WAWQI), checks BIS 10500:2012 threshold violations, and handles lab vs sensor categorization.
4. **`WeatherService.java`**: Fetches weather data, coordinates Open-Meteo REST API requests, and computes monsoon season statistics.
5. **`DiseaseService.java`**: Epidemiological surveillance service aggregating cases and calculating standardized incidence rates per 100,000 population.
6. **`PredictionService.java`**: Orchestrates input feature validation, invokes Weka ML inference, assigns WHO/IDSP public health advisories, and stores prediction audits.
7. **`WekaModelService.java`**: Embedded Java ML inference engine loading ARFF datasets, training Random Forest classifiers, and evaluating 10-fold cross-validation.
8. **`SparkDataProcessingService.java`**: Big Data bridge executing PySpark 4.2.0 pipelines via `ProcessBuilder`, auditing execution metrics to `spark_job_history`.
9. **`DataPreprocessingService.java`**: Cleaning engine handling deduplication, range validation, missing value imputation, and ARFF dataset export.
10. **`DatasetImportService.java`**: OpenCSV-based multi-format CSV parser and ingestion pipeline for water, weather, disease, and population datasets.
11. **`AlertService.java`**: Automated surveillance alert generator based on threshold breaches and prediction risk categories.
12. **`PopulationService.java`**: Manages demographic histories, Census 2011 baselines, and CAGR projections.
13. **`CustomUserDetailsService.java`**: Spring Security authentication provider loading user credentials and authorities.

---

## 5. All REST API Endpoints (57 Endpoints)

| Controller | Base Path | Method | Endpoint | Description |
|---|---|---|---|---|
| `AlertController` | `/api/alerts` | `GET` | `/` | Get all active surveillance alerts |
| `AlertController` | `/api/alerts` | `GET` | `/unread` | Get unread alerts |
| `AlertController` | `/api/alerts` | `GET` | `/unread/count` | Count of unread alerts |
| `AlertController` | `/api/alerts` | `PUT` | `/{id}/read` | Mark single alert as read |
| `AlertController` | `/api/alerts` | `PUT` | `/read-all` | Mark all alerts as read |
| `AuthController` | `/api/auth` | `POST` | `/login` | Authenticate user with BCrypt credentials |
| `AuthController` | `/api/auth` | `GET` | `/me` | Get currently logged-in user profile |
| `DashboardController` | `/api/dashboard` | `GET` | `/summary` | Get statewide KPI summary metrics |
| `DashboardController` | `/api/dashboard` | `GET` | `/disease-trends` | Monthly disease trend data |
| `DashboardController` | `/api/dashboard` | `GET` | `/water-quality` | Water quality risk breakdown |
| `DashboardController` | `/api/dashboard` | `GET` | `/risk-distribution` | Risk tier breakdown (Low/Med/High) |
| `DashboardController` | `/api/dashboard` | `GET` | `/district-risk` | District-by-district risk scores |
| `DashboardController` | `/api/dashboard` | `GET` | `/live-stream` | Real-time Server-Sent Events (SSE) stream |
| `DatasetController` | `/api/datasets` | `POST` | `/upload` | Ingest and validate custom CSV dataset |
| `DatasetController` | `/api/datasets` | `GET` | `/` | Get import audit history logs |
| `DatasetController` | `/api/datasets` | `GET` | `/sources` | Get official dataset sources registry |
| `DatasetController` | `/api/datasets` | `GET` | `/quality` | Get dataset quality and completeness reports |
| `DiseaseController` | `/api/diseases` | `GET` | `/` | Paginated disease surveillance records |
| `DiseaseController` | `/api/diseases` | `GET` | `/analytics` | Cumulative cases by pathogen |
| `DiseaseController` | `/api/diseases` | `GET` | `/incidence` | District-wise incidence rates per 100k |
| `DiseaseController` | `/api/diseases` | `GET` | `/totals` | Statewide total cases |
| `DiseaseController` | `/api/diseases` | `GET` | `/district-wise` | Cases grouped by district |
| `DistrictController` | `/api/districts` | `GET` | `/` | Master list of all 38 districts |
| `DistrictController` | `/api/districts` | `GET` | `/{name}` | Single district detailed metadata |
| `DistrictController` | `/api/districts` | `GET` | `/summary` | Statewide demographic and area summary |
| `ForecastController` | `/api/forecasts` | `GET` | `/` | 7-day and 30-day ahead projections |
| `ForecastController` | `/api/forecasts` | `GET` | `/metrics` | Time-series error metrics (MAE, RMSE, MAPE) |
| `ModelController` | `/api/model` | `POST` | `/train` | Trigger Weka Random Forest retraining |
| `ModelController` | `/api/model` | `GET` | `/evaluation` | Get 10-fold CV confusion matrix and metrics |
| `ModelController` | `/api/model` | `GET` | `/comparison` | Benchmark comparison (RF vs GB vs ET) |
| `PopulationController` | `/api/population` | `GET` | `/` | Demographic records for all districts |
| `PopulationController` | `/api/population` | `GET` | `/{district}` | Single district demographic history |
| `PredictionController` | `/api/predictions` | `POST` | `/predict` | Real-time multi-indicator ML evaluation |
| `PredictionController` | `/api/predictions` | `GET` | `/history` | Historical prediction logs |
| `PreprocessingController`| `/api/preprocessing`| `POST`| `/run` | Run deduplication, cleaning, and ARFF sync |
| `ReportController` | `/api/reports` | `GET` | `/` | Executive public health report generation |
| `SystemController` | `/api/system` | `GET` | `/health` | System health check and component status |
| `SystemController` | `/api/system` | `POST` | `/spark/process` | Trigger distributed PySpark execution |
| `SystemController` | `/api/system` | `GET` | `/spark/history` | Spark job execution audit history |
| `SystemController` | `/api/system` | `GET` | `/spark/report` | Latest Spark execution telemetry JSON |
| `UserController` | `/api/users` | `GET` | `/` | List all platform users |
| `UserController` | `/api/users` | `POST` | `/` | Create new user account |
| `WaterQualityController`| `/api/water-quality`| `GET` | `/` | Paginated water quality observations |
| `WaterQualityController`| `/api/water-quality`| `GET` | `/analytics` | Statewide WQI rankings and metrics |
| `WaterQualityController`| `/api/water-quality`| `GET` | `/violations` | BIS 10500:2012 threshold violations |
| `WeatherController` | `/api/weather` | `GET` | `/current` | Current weather for single district |
| `WeatherController` | `/api/weather` | `GET` | `/current/all` | Current weather across all districts |
| `WeatherController` | `/api/weather` | `GET` | `/` | Paginated historical weather observations |
| `WeatherController` | `/api/weather` | `GET` | `/analytics` | Temperature/rainfall trends and SMAs |
| `WeatherController` | `/api/weather` | `GET` | `/monsoon` | Southwest and Northeast monsoon analytics |

---

## 6. Database Tables and Relationships (17 Relational Tables)

- **`districts`**: Centralized master table for all 38 districts with GPS coordinates, areas, Census 2011 baselines, and CAGR projections.
- **`dataset_sources`**: Provenance registry tracking official portals (CGWB, IMD, Census, IDSP) and data classification.
- **`water_quality_records`**: 2,698 chemical observations with station type (lab vs sensor) and missingness flags.
- **`weather_records`**: 2,698 daily meteorological records with IMD/Open-Meteo attribution.
- **`population_records`**: Demographic records with rural/urban split and sanitation coverage.
- **`disease_records`**: 2,698 surveillance records tracking Cholera, Typhoid, ADD, Hepatitis with suspected vs confirmed breakdown.
- **`data_quality_reports`**: Data validation audits (completeness rate, duplicates, outliers).
- **`processed_analytics`**: Aggregated monthly WQI and disease incidence metrics.
- **`ml_models`**: Metadata repository for active ML model versions and benchmark scores.
- **`model_training_history`**: Audit trail of model training executions and split strategies.
- **`prediction_results`**: Historical ML inference logs with disease-specific risk likelihoods and WHO advisories.
- **`disease_forecasts`**: 7-day and 30-day ahead prospective outbreak projections with 95% confidence intervals.
- **`surveillance_alerts`**: Real-time alerts categorized by severity (`CRITICAL`, `WARNING`, `INFO`).
- **`alert_history`**: Notification dispatch logs for field health officers.
- **`spark_job_history`**: Audit history of distributed PySpark DataFrame transformations.
- **`users`**: Role-based access accounts (`ADMIN`, `USER`/Analyst, `DHO`).
- **`reports`**: Generated executive summaries and audit snapshots.

---

## 7. Dataset Locations and Record Counts

| Dataset File | Storage Path | Layer | Record Count | Attributes | Time Period |
|---|---|---|---|---|---|
| `districts_master.csv` | `data/raw/` & `data/cleaned/` | Master | 38 | 15 columns | 2011, 2021, 2025, 2026 |
| `dataset_sources.csv` | `data/raw/` & `data/cleaned/` | Provenance | 5 | 12 columns | 2021–2026 |
| `water_quality_raw.csv` | `data/raw/` | Raw | 2,698 | 22 columns | 2021–2026 |
| `water_quality_cleaned.csv`| `data/cleaned/` | Cleaned | 2,698 | 19 columns | 2021–2026 |
| `weather_raw.csv` | `data/raw/` | Raw | 2,698 | 15 columns | 2021–2026 |
| `weather_cleaned.csv` | `data/cleaned/` | Cleaned | 2,698 | 13 columns | 2021–2026 |
| `disease_surveillance_raw.csv`| `data/raw/` | Raw | 2,698 | 14 columns | 2021–2026 |
| `disease_cleaned.csv` | `data/cleaned/` | Cleaned | 2,698 | 13 columns | 2021–2026 |
| `training_dataset.csv` | `ml/training/` | ML Feature | 2,698 | 12 columns | 2021–2026 |
| `training_dataset.arff` | `ml/training/` | Weka ARFF | 2,698 | 12 attributes | 2021–2026 |
| `tamilnadu_epidemiological_analytics.parquet` | `data/processed/parquet/` | Parquet | 2,698 | 20 columns | 2021–2026 (10 Partitions) |

---

## 8. Machine Learning Models and Input Features

- **Input Features (10):**
  1. `turbidity`: Water clarity and suspended microbial harboring particles (NTU).
  2. `rainfall`: 24-hour precipitation driving surface runoff (mm).
  3. `wqi`: Weighted Arithmetic Water Quality Index based on BIS 10500 standards.
  4. `temperature`: Ambient temperature driving pathogen replication ($^\circ\text{C}$).
  5. `tds`: Total Dissolved Solids reflecting mineral and salinity intrusion (mg/L).
  6. `population_density`: Urban crowding and sewage infrastructure pressure.
  7. `sanitation_coverage`: Functional household latrine coverage percentage.
  8. `dissolved_oxygen`: Aerobic water health index (mg/L).
  9. `ph`: Acid/base deviation from neutral 7.0.
  10. `rainfall_7d_sma`: 7-day lagged rainfall moving average.
- **Target Classes:** `LOW`, `MEDIUM`, `HIGH`.
- **Chronological Split:** 2021-01-01 to 2024-12-31 (1,824 train samples), 2025-01-01 to 2026-11-11 (874 test samples). Zero future leakage.
- **Model Algorithms Evaluated:**
  - Gradient Boosting Classifier (97.94% Acc, 0.9981 ROC-AUC)
  - Random Forest Classifier (96.80% Acc, 0.9955 ROC-AUC)
  - Extra Trees Classifier (91.30% Acc, 0.9620 ROC-AUC)
  - Disease-Specific Binary Classifiers: Cholera (92.89%), Typhoid (92.24%), ADD (89.75%), Hepatitis (91.40%)
  - Time-Series Outbreak Forecasting: Exponential Trend Smoothing (MAE: 34.55, RMSE: 49.33, MAPE: 14.8%)

---

## 9. Spark Processing Workflow

The PySpark pipeline in `spark/spark_processor.py`:
1. Initializes `SparkSession` with local multi-threading or standalone cluster master.
2. Ingests raw water, weather, and disease CSVs with explicit schemas.
3. Performs Spark SQL range validation and deduplication on `[district, observation_date]`.
4. Executes broadcast hash join with `districts_master` (broadcast small table to avoid shuffles).
5. Computes WAWQI, 7-day and 30-day moving averages using Spark Window functions.
6. Exports partitioned dataset to `data/processed/parquet/` partitioned by `[district_name, year]`.
7. Exports execution telemetry to `data/processed/spark_execution_report.json` and logs to database.

---

## 10. Live API Integrations

- **Open-Meteo REST API:**
  - Integrated via `WeatherService.java` using Spring `RestTemplate` with 2000ms connect/read timeouts.
  - Queries Open-Meteo endpoint using district latitude and longitude coordinates.
  - Retrieves live temperature, relative humidity, precipitation, and wind speed.
  - Graceful fallback: If network connection is offline or API fails, returns latest verified stored observation flagged as `OFFICIAL_HISTORICAL` rather than failing.

---

## 11. Authentication and Authorization Implementation

- **Spring Security 6.2.x Configuration (`SecurityConfig.java`):**
  - BCrypt password encoder with salt.
  - DAO authentication provider backed by `CustomUserDetailsService`.
  - Roles supported: `ADMIN`, `USER` (Public Health Analyst), `DHO` (District Health Officer).
  - Web paths permitted: Public static assets and authentication endpoints (`/api/auth/**`).
  - Protected API routes: System commands, ML retraining, user management, and dataset modifications.

---

## 12. Existing Errors & Broken Functionalities Identified in Audit

1. **Missing `DELETE /api/users/{id}` Endpoint:**
   - In `UserController.java`, only `GET` and `POST` were defined. The frontend `users.html` calls `api.deleteUser(id)` (`DELETE /api/users/${id}`), resulting in HTTP 405 Method Not Allowed error.
2. **SSE Event Name Mismatch in Dashboard:**
   - `DashboardService.java` scheduled method pushed events using name `METRICS_UPDATE`.
   - `dashboard.js` event listener explicitly listened for `DASHBOARD_UPDATE`.
   - Result: Dashboard live updates pushed by server were silently dropped by the browser.
3. **WeatherService District Coordinate Resolution:**
   - `WeatherService.java` queried `PopulationDataRepository` for coordinates, which was the legacy 10-district table, rather than the centralized `DistrictRepository` containing all 38 districts.
4. **Live Weather Observations Not Persisted to Database:**
   - When live weather was fetched from Open-Meteo, it was returned in-memory but not saved into `weather_records` with observation timestamps and synchronization metadata.
5. **Missing Automatic Data Synchronization Service:**
   - There was no scheduled synchronization service tracking connection status, last sync timestamp, records retrieved, and failed requests, and no `POST /api/system/sync` endpoint for manual refresh.
6. **Lack of District-Level Authorization for District Health Officers:**
   - A DHO logged into the system could view data from all 38 districts instead of being restricted to their assigned district.
7. **User Entity Lacks `assignedDistrict` and `active` Fields:**
   - `User.java` had no field to link a DHO to their specific district jurisdiction, and no boolean to deactivate accounts without deleting them.

---

## 13. Missing Implementations

1. Dedicated `DataSyncService.java` with automatic synchronization, manual refresh endpoint (`POST /api/system/sync`), and sync status telemetry (`GET /api/system/sync-status`).
2. UI Live Synchronization Banner and "Refresh Data" button on the Dashboard and Weather pages.
3. Explicit distinction in Water Quality between periodic CGWB laboratory groundwater tests (`station_type: GROUNDWATER_LAB`) and continuous municipal sensors (`DRINKING_WATER_SENSOR`), displaying station metadata and data freshness.
4. User management endpoints: `DELETE /api/users/{id}` and `PUT /api/users/{id}/toggle-status`.
5. Automated validation script `scripts/validate_datasets.py` and test report `DATASET_VALIDATION_REPORT.md` with complete 38-district coverage matrix.

---

## 14. Duplicate or Unused Code

- Redundant legacy entity `PopulationData` and repository `PopulationDataRepository` retained for backward compatibility alongside modern `PopulationRecord` and `District`.
- `WeatherData` / `WeatherDataRepository` and `WaterQuality` / `WaterQualityRepository` duplicate `WeatherRecord` and `WaterQualityRecord`.
- Redundant demo generation script `generate_demo_data.py` superseded by `scripts/generate_tamilnadu_dataset.py`.

---

## 15. Security Issues

1. **Insecure Default Passwords in Production:** Initial seed passwords (`admin123`, `user123`) must be documented with explicit instructions to change upon production deployment.
2. **Unrestricted District Access for DHOs:** Without backend district validation, a DHO could potentially modify or view surveillance data for districts outside their jurisdiction.
3. **CORS Allow All (`@CrossOrigin(origins = "*")`):** Permissive CORS in development needs documentation for production domain lock-down.

---

## 16. Recommended Prioritized Fixes

1. **Implement `DataSyncService.java`**: Periodic and on-demand synchronization fetching live observations for all 38 districts from Open-Meteo, storing them in `weather_records`, logging sync timestamps, connection status, and error counts.
2. **Update `User.java` and `UserController.java`**: Add `assignedDistrict` and `active` status fields, implement `DELETE /api/users/{id}` and status toggle, and enforce DHO district restrictions in controllers.
3. **Fix SSE Event Nismatch in `dashboard.js` & `DashboardService.java`**: Align SSE event names (`DASHBOARD_UPDATE` and `METRICS_UPDATE`) and ensure live indicator activates upon stream reception.
4. **Update `WeatherService.java`**: Inject `DistrictRepository` to resolve exact coordinates for all 38 districts.
5. **Add Live Data Refresh UI & Status Indicators**: Add manual refresh buttons, live connection status indicators, and data freshness badges across Dashboard, Weather, and Water Quality modules.
6. **Execute Automated Verification Suite**: Run dataset validation, Big Data test, ML test, and system test scripts, generating reports: `DATASET_VALIDATION_REPORT.md`, `BIG_DATA_TEST_REPORT.md`, `ML_TEST_REPORT.md`, and `SYSTEM_TEST_REPORT.md`.
7. **Author Mandatory `USER_MANUAL.md`**: 16 comprehensive operational sections for Admin, Analyst, and DHO roles.

