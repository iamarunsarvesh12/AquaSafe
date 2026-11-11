# AquaSafe: Comprehensive System User Manual & Operational Handbook

**Platform Name:** AquaSafe – Tamil Nadu Water-Borne Disease Prediction & Big Data Surveillance  
**Applicable Release:** Version 2.4.0 (Enterprise Edition)  
**Target Coverage:** All 38 Revenue Districts of Tamil Nadu, India (2021–2026)  
**Document Revision:** November 2026  
**Audience:** System Administrators, State Epidemiologists, Public Health Analysts, District Health Officers (DHOs)

---

# Table of Contents
1. [1. Introduction & Mission Overview](#1-introduction--mission-overview)
2. [2. System Requirements & Prerequisites](#2-system-requirements--prerequisites)
3. [3. How to Start the Application](#3-how-to-start-the-application)
4. [4. Authentication & Login Guide](#4-authentication--login-guide)
5. [5. User Roles and Permissions Matrix](#5-user-roles-and-permissions-matrix)
6. [6. Administrator Operational Guide](#6-administrator-operational-guide)
7. [7. Public Health Analyst Operational Guide](#7-public-health-analyst-operational-guide)
8. [8. District Health Officer (DHO) Operational Guide](#8-district-health-officer-dho-operational-guide)
9. [9. Comprehensive Module-by-Module Guide (All 14 Modules)](#9-comprehensive-module-by-module-guide-all-14-modules)
10. [10. Live Data Tracking & Synchronization Guide](#10-live-data-tracking--synchronization-guide)
11. [11. Dataset Management & Ingestion Guide](#11-dataset-management--ingestion-guide)
12. [12. Machine Learning & Predictive Risk Guide](#12-machine-learning--predictive-risk-guide)
13. [13. Big Data Processing with Apache Spark](#13-big-data-processing-with-apache-spark)
14. [14. System Troubleshooting & Fault Recovery](#14-system-troubleshooting--fault-recovery)
15. [15. Frequently Asked Questions (FAQ)](#15-frequently-asked-questions-faq)
16. [16. Security, Compliance & Public Health Disclaimer](#16-security-compliance--public-health-disclaimer)

---

# 1. Introduction & Mission Overview

**AquaSafe** is an enterprise-grade epidemiological surveillance and big data analytics platform tailored specifically for the state of **Tamil Nadu, India**.
The platform ingests, harmonizes, analyzes, and learns from multi-source historical datasets (2021–2025) and real-time live data feeds (2026) spanning all **38 revenue districts**.

### Core Mission Capabilities:
- **Early Warning Outbreak Detection:** Predict water-borne disease outbreaks (Cholera, Typhoid, Acute Diarrhoeal Disease, Hepatitis) before clinical hospital admissions peak.
- **Scientific Water Quality Index (WQI):** Evaluate water safety using Bureau of Indian Standards (BIS 10500:2012) criteria, factoring in pH, turbidity, Total Dissolved Solids (TDS), and dissolved oxygen.
- **Distributed Big Data Engine:** Harness Apache Spark 4.2.0 and PySpark to execute distributed joins, aggregations, and generate Snappy-compressed partitioned Parquet files.
- **Prospective Time-Series Forecasting:** Project forward disease caseloads over 7-day tactical alert horizons and 30-day health capacity planning windows.
- **Role-Based Access Control (RBAC):** Provide tailored workspaces for System Administrators, State Epidemiologists, and District Health Officers (DHOs) with strict geographic jurisdiction boundaries.

---

# 2. System Requirements & Prerequisites

### 2.1 Hardware Requirements
| Resource | Minimum Specification | Recommended Production Specification |
|---|---|---|
| **CPU Architecture** | x86_64 Dual-Core (2.0 GHz) | Quad-Core / 8-Thread Intel Core i7 / AMD Ryzen 7 |
| **RAM** | 8 GB RAM | 16 GB RAM or higher |
| **Storage** | 10 GB free disk space (SSD) | 50 GB NVMe SSD |
| **Network** | 5 Mbps Internet connection | 50 Mbps stable broadband |
| **Display** | 1280 x 720 resolution | 1920 x 1080 Full HD |

### 2.2 Software Environment
- **Operating System:** Windows 10/11, Ubuntu Linux 20.04+, macOS 12+
- **Java Runtime:** OpenJDK 17 LTS (Java 17.0.12 or later)
- **Python Runtime:** Python 3.10 or 3.11 with `pip`
- **Build Tool:** Apache Maven 3.8+ (bundled or installed)
- **Web Browser:** Google Chrome 100+, Mozilla Firefox 95+, Microsoft Edge 100+
- **Optional External Database:** MySQL 8.0+ (Platform defaults to persistent H2 in-file database if MySQL is inactive)

---

# 3. How to Start the Application

AquaSafe provides unified startup scripts across all primary terminal environments.

### 3.1 PowerShell (Recommended on Windows)
Open PowerShell as Administrator in the project root directory and execute:
```powershell
.\run.ps1
```
*To pass custom options:*
```powershell
.\run.ps1 -Port 8080 -SparkMaster "local[*]"
```

### 3.2 Windows Command Prompt (cmd.exe)
```cmd
run.bat
```

### 3.3 Python Universal Launcher (Cross-Platform)
```bash
python run.py
```

### 3.4 Linux / macOS Shell Script
```bash
chmod +x run.sh
./run.sh
```

### 3.5 Manual Multi-Tier Startup (Development Mode)
If running components independently:
1. **Start Spring Boot Backend:**
   ```bash
   mvn spring-boot:run -f backend/pom.xml
   ```
2. **Access Web Application:**
   Open browser and navigate to: `http://localhost:8080/`

---

# 4. Authentication & Login Guide

AquaSafe implements secure session-based authentication with role-aware navigational routing.

### 4.1 Login URL
Navigate to:
```
http://localhost:8080/login.html
```

### 4.2 Pre-Configured Test Credentials
| Role Category | Username | Password | Assigned District Jurisdiction | Primary Responsibilities |
|---|---|---|---|---|
| **System Administrator** | `admin` | `admin123` | **All Districts (Statewide)** | User management, Spark analytics execution, model retrain, database backups |
| **Public Health Analyst** | `analyst` | `user123` | **All Districts (Statewide)** | Epidemiological dashboards, ML risk inference, time-series forecasts, reports |
| **District Health Officer** | `dho` | `user123` | **Chennai (Strict Jurisdiction)** | Localized alert monitoring, PHC action protocols, field intervention reports |

### 4.3 Login Procedure:
1. Enter your assigned **Username** and **Password** into the respective fields.
2. Alternatively, click any of the **Quick Fill Credential Buttons** on `login.html` to instantly load demo credentials.
3. Click the **"Sign In"** button.
4. The system validates credentials, verifies the account is `ACTIVE`, checks the `assignedDistrict`, stores the auth token in browser session storage, and redirects to `dashboard.html`.
5. If an account is deactivated, an alert banner displays: *"Account is deactivated. Please contact your system administrator."*

---

# 5. User Roles and Permissions Matrix

| Feature / Module Capability | Administrator (`ADMIN`) | Public Health Analyst (`USER`) | District Health Officer (`DHO`) |
|---|---|---|---|
| **View Statewide Dashboard** | Yes | Yes | Limited to Assigned District |
| **Explore 38 Districts (360 Explorer)** | Yes (All 38) | Yes (All 38) | Read-only (Jurisdiction highlight) |
| **Execute ML Risk Inference** | Yes | Yes | Yes (Restricted to district data) |
| **View Prospective Time-Series Forecasts**| Yes | Yes | Yes (District slice) |
| **Trigger Apache Spark Pipeline** | **Yes** | No (Read results only) | No |
| **Trigger Machine Learning Retraining** | **Yes** | No | No |
| **User Administration (Create/Delete/Deactivate)**| **Yes** | No | No |
| **Manual Live Data Sync (Open-Meteo)** | **Yes** | Yes | No |
| **Upload Raw Surveillance CSV Datasets** | **Yes** | No | No |
| **Download PDF/CSV Epidemiological Reports**| Yes | Yes | Yes (District report) |

---

# 6. Administrator Operational Guide

Administrators maintain complete authority over the system platform, data pipelines, and user accounts.

### 6.1 Managing User Accounts (`users.html`)
1. Navigate to **"User Management"** from the navigation bar or access `/users.html`.
2. **Create New User:**
   - Click the **"Create User"** button.
   - Enter **Username**, **Email**, **Full Name**, **Password**, and select a **Role** (`ADMIN`, `USER`, or `DHO`).
   - If `DHO` is selected, choose the specific **Assigned District** from the 38-district dropdown list (e.g. `Coimbatore`, `Madurai`, `Chennai`).
   - Click **"Save User"**.
3. **Toggle Active / Inactive Status:**
   - Locate the target user in the User Directory table.
   - Click the **"Deactivate"** or **"Activate"** button.
   - Deactivated users immediately lose authentication access and receive HTTP 403 upon attempting login.
4. **Delete User:**
   - Click the **"Delete"** button next to a user.
   - Confirm the prompt.
   - *Security Note:* The primary root administrator (`admin`) cannot be deleted or deactivated.

### 6.2 Running Big Data Spark Analytics (`spark.html`)
1. Navigate to **"Big Data Analytics"** (`/spark.html`).
2. Review the status of the last executed Spark job, including input records (8,094), processed records (2,698), Parquet storage size, and execution duration.
3. To trigger a new distributed execution across all 38 districts:
   - Click **"Run Spark Distributed Pipeline"**.
   - The backend dispatches `AquaSafeSparkJob` / `spark_processor.py`.
   - Live console logs and execution metrics update automatically upon job termination.
   - Partitioned Parquet files are written to `data/processed/parquet/`.

### 6.3 Retraining Machine Learning Models (`model.html`)
1. Navigate to **"Model Retraining"** (`/model.html`).
2. Review the current champion model (`Gradient Boosting` with 97.94% accuracy, 97.92% F1-score).
3. Click **"Retrain Models on Latest 5-Year Data"**.
4. The system executes chronological validation (2021–2024 train, 2025–2026 test), updates model binaries in `ml/models/`, and generates updated evaluation metrics.

---

# 7. Public Health Analyst Operational Guide

Analysts focus on surveillance analytics, pattern recognition, epidemiological risk scoring, and trend forecasting.

### 7.1 Real-Time Risk Inference (`prediction.html`)
1. Navigate to **"Risk Prediction"** (`/prediction.html`).
2. Select target **District** from the 38-district dropdown.
3. Input physical and meteorological indicators:
   - **pH** (Normal: 6.5–8.5)
   - **Turbidity** (NTU, Standard: $\le 5.0$ NTU)
   - **TDS** (mg/L, Standard: $\le 500$ mg/L)
   - **Dissolved Oxygen** (mg/L, Standard: $\ge 5.0$ mg/L)
   - **Ambient Temperature** (°C)
   - **Rainfall** (mm/24h)
   - **Relative Humidity** (%)
   - **Previous Disease Caseload**
4. Click **"Run Disease Risk Assessment"**.
5. The system computes:
   - **Scientific Water Quality Index (WQI)**
   - **Overall Risk Categorization** (`LOW`, `MEDIUM`, or `HIGH`)
   - **Outbreak Probability** (e.g. 84.5% High Risk)
   - **Pathogen Breakdown Probabilities** for Cholera, Typhoid, Acute Diarrhea, and Hepatitis.
   - **Clinical Advisory & Recommended Public Health Action**.

### 7.2 Prospective Caseload Forecasting (`forecast.html`)
1. Navigate to **"Forecasting"** (`/forecast.html`).
2. Select the target district or view the Statewide summary.
3. Toggle between **7-Day Tactical Horizon** and **30-Day Strategic Horizon**.
4. The interactive chart illustrates:
   - Historical observed trend (solid line)
   - Prospective forecast mean (dashed line)
   - 95% Confidence Upper and Lower Bounds (shaded region)
5. Review the automated health alert badge (e.g., *"Elevated Inundation Hazard – High Chlorination Protocol Recommended"*).

---

# 8. District Health Officer (DHO) Operational Guide

The District Health Officer role is restricted to a designated geographic jurisdiction (e.g., Chennai).

### 8.1 Jurisdiction Detection & Visual Banner
- Upon logging in as `dho`, a persistent amber banner appears at the top of the interface:
  > **Restricted Jurisdiction Mode: Chennai District Health Officer**  
  > *You are authorized to review localized field surveillance and generate intervention protocols for Chennai district only.*
- All dashboard metrics, alert counts, and charts default automatically to the assigned district.

### 8.2 Daily Surveillance Routine for DHO
1. **Morning Review:** Check `dashboard.html` for overnight live weather updates and rainfall accumulations.
2. **Water Safety Audit:** Open `water-quality.html` to review recent Central Ground Water Board (CGWB) hydrograph station audit records in your district.
3. **Alert Actioning:** Open `alerts.html`. Review active health warnings. If a High-Risk alert appears, trigger the localized standard operating protocol (mobile chlorination, boiling advisories, PHC medical reserves).
4. **Export Incident Report:** Open `reports.html`, select your district, choose the reporting period, and click **"Export Official PDF Report"** for submission to the Directorate of Public Health (DPH).

---

# 9. Comprehensive Module-by-Module Guide (All 14 Modules)

### Module 1: `index.html` – System Landing Page
- **Purpose:** Public landing page summarizing system architecture, mission, state coverage, and access portals.
- **Access:** Public (All users).
- **Key Actions:** Quick links to "Login", "System Guide", and "Data Provenance".

### Module 2: `login.html` – Authentication Portal
- **Purpose:** Secure gateway for authenticated role sessions.
- **Access:** Public.
- **Inputs:** Username, Password.
- **Buttons:** "Sign In", Quick Credential Buttons (`admin`, `analyst`, `dho`).
- **Expected Output:** Authenticated redirect to `dashboard.html`.

### Module 3: `dashboard.html` – Statewide Surveillance Dashboard
- **Purpose:** High-level command center displaying statewide health indicators, active outbreak counts, live weather sync status, and district risk ranks.
- **Access:** All authenticated roles.
- **Inputs:** State/District filter toggle.
- **Buttons:** "Refresh Live Data", "Export Summary".
- **Expected Output:** Real-time KPI cards, SSE live stream updates, and district epidemiological rankings.

### Module 4: `water-quality.html` – Water Quality & BIS Compliance
- **Purpose:** Tracks pH, turbidity, TDS, dissolved oxygen, chloride, nitrate, and fluoride against BIS 10500:2012 safety standards.
- **Access:** All authenticated roles.
- **Inputs:** District dropdown, parameter selector.
- **Expected Output:** Provenance badges (`CGWB Hydrograph Lab Station`), parameter deviation charts, and BIS violation counts.

### Module 5: `weather.html` – Meteorological Surveillance
- **Purpose:** Monitors temperature, 24-hour rainfall, humidity, and wind speed across all 38 districts.
- **Access:** All authenticated roles.
- **Inputs:** District selector, date range.
- **Buttons:** "Refresh Open-Meteo Observations".
- **Expected Output:** Historical vs latest live weather observation charts and rainfall runoff hazard index.

### Module 6: `disease.html` – Disease Surveillance & Outbreak Tracking
- **Purpose:** Tracks historical and active incidence of Cholera, Typhoid, Acute Diarrhoeal Disease, and Hepatitis.
- **Access:** All authenticated roles.
- **Inputs:** District filter, pathogen filter, date range.
- **Expected Output:** Case counts, incidence rates per 100,000 population, and outbreak epicenter detection.

### Module 7: `districts.html` – District 360 Explorer
- **Purpose:** Comprehensive deep-dive explorer for all 38 individual districts of Tamil Nadu.
- **Access:** All authenticated roles.
- **Inputs:** Dropdown of all 38 districts.
- **Expected Output:** District demographic profile (Census + CAGR estimates), historical water chemistry, rainfall history, and health facility capacity.

### Module 8: `map.html` – Tamil Nadu GIS Epidemiological Heatmap
- **Purpose:** Interactive geospatial visualization of disease incidence and water contamination hotspots across the state.
- **Access:** All authenticated roles.
- **Inputs:** Disease layer toggle, water layer toggle, year slider.
- **Expected Output:** Color-coded district polygons with clickable telemetry popup modals.

### Module 9: `prediction.html` – ML Risk Inference Engine
- **Purpose:** Executes machine learning inference on custom or real-time environmental inputs.
- **Access:** All authenticated roles.
- **Inputs:** 8 environmental and meteorological fields.
- **Buttons:** "Run Disease Risk Assessment", "Reset Inputs".
- **Expected Output:** WQI score, Risk level (`LOW`/`MEDIUM`/`HIGH`), probability breakdown, and recommended public health protocol.

### Module 10: `model.html` – Model Performance & Retraining
- **Purpose:** Inspects algorithm benchmarks (Random Forest, Gradient Boosting, Extra Trees), confusion matrices, feature importances, and triggers model retraining.
- **Access:** `ADMIN` (retrain), `USER`/`DHO` (read-only metrics).
- **Buttons:** "Retrain Models on Latest Data".
- **Expected Output:** Model accuracy table, ROC curves, feature importance ranking, and retraining execution logs.

### Module 11: `forecast.html` – Prospective Time-Series Forecaster
- **Purpose:** Multi-horizon (7-day and 30-day) caseload forecasting with statistical confidence intervals.
- **Access:** All authenticated roles.
- **Inputs:** District selector, horizon toggle (7 vs 30 days).
- **Expected Output:** Projected caseload with upper and lower 95% confidence bands and MAE/RMSE telemetry.

### Module 12: `spark.html` – Apache Spark Big Data Engine
- **Purpose:** Monitors distributed Spark analytics execution, multi-source joins, and partitioned Parquet columnar storage.
- **Access:** `ADMIN` (execute job), `USER`/`DHO` (view execution reports).
- **Buttons:** "Run Spark Distributed Pipeline".
- **Expected Output:** Ingestion counters (8,094 inputs), joined record counters (2,698 outputs), partition maps, and execution latency.

### Module 13: `data.html` – Dataset Management & Upload Portal
- **Purpose:** Manages raw and cleaned multi-source surveillance datasets and provides verified CSV upload ingestion.
- **Access:** `ADMIN`.
- **Inputs:** CSV file upload form, dataset category selector.
- **Buttons:** "Upload Dataset", "Verify Schema Integrity".
- **Expected Output:** Record validation reports, duplicate checks, and dataset inventory status.

### Module 14: `reports.html` – Epidemiological Surveillance Reports
- **Purpose:** Generates comprehensive PDF and CSV reports for state health officials and district administrations.
- **Access:** All authenticated roles.
- **Inputs:** District selector, report format, time window.
- **Buttons:** "Generate Official Surveillance Report", "Export CSV".
- **Expected Output:** Downloadable, formatted epidemiological intelligence dossiers.

### Module 15: `alerts.html` – Early Warning Public Health Alerts
- **Purpose:** Real-time triage center for water contamination and disease outbreak alerts.
- **Access:** All authenticated roles.
- **Inputs:** Severity filter (`HIGH`, `MEDIUM`, `LOW`), district filter.
- **Buttons:** "Acknowledge Alert", "Dispatch PHC Notice".
- **Expected Output:** Priority alert cards with actionable intervention steps.

### Module 16: `users.html` – Role-Based User Management
- **Purpose:** User account provisioning, role assignment, district jurisdiction lockdown, and active/inactive account toggling.
- **Access:** `ADMIN` only.
- **Inputs:** User creation modal with district assignment.
- **Buttons:** "Create User", "Activate/Deactivate", "Delete User".
- **Expected Output:** Tabular user registry with live status indicators.

---

# 10. Live Data Tracking & Synchronization Guide

AquaSafe maintains a strict scientific boundary between:
1. **Historical Records:** 5 complete years of verified observations (2021–2025).
2. **Current Ground Observations:** Periodic Central Ground Water Board (CGWB) hydrograph laboratory audits for 2026.
3. **Live Meteorological Observations:** Real-time hourly and daily feeds from the Open-Meteo API.
4. **Prospective Forecasts:** Mathematical extrapolations with explicit confidence intervals.

### 10.1 Groundwater Lab Audit vs Continuous Sensor Distinction
- **Important Public Health Principle:** Groundwater quality readings are obtained through physical chemical titration and spectroscopy conducted at certified district water testing laboratories.
- AquaSafe explicitly tags these observations as `GROUNDWATER_LAB` audit mode.
- The platform **never fabricates high-frequency sensor readings** for laboratory-sampled groundwater sources.

### 10.2 Open-Meteo Weather Synchronization
- **Automatic Background Sync:** Runs every 5 minutes in background (`DataSyncService.java`).
- **Manual Trigger:** Click **"Refresh Live Data"** on `dashboard.html` or `weather.html`.
- **Duplicate Prevention:** If a weather observation for today's date already exists for a district, the synchronization service updates the record in-place rather than creating duplicate rows.
- **Telemetry Indicators:** The header displays:
  - `Live API Status:` **ONLINE** (green indicator)
  - `Observation Timestamp:` Records the exact UTC/IST fetch timestamp.
  - `Sync Success Count:` 38/38 districts.

---

# 11. Dataset Management & Ingestion Guide

AquaSafe manages four core datasets residing in `data/raw/` and `data/cleaned/`:

### 11.1 The Four Master Surveillance Datasets
1. **Water Quality Dataset (`water_quality_cleaned.csv`):**
   - 2,698 observations covering all 38 districts (2021–2026).
   - Parameters: pH, turbidity, TDS, dissolved oxygen, electrical conductivity, chloride, nitrate, fluoride, water temperature.
   - Provenance: Central Ground Water Board (CGWB) and National Water Informatics Centre (NWIC).
2. **Meteorological Dataset (`weather_cleaned.csv`):**
   - 2,698 observations covering all 38 districts (2021–2026).
   - Parameters: Minimum temperature, maximum temperature, average temperature, 24h rainfall, humidity, wind speed.
   - Provenance: India Meteorological Department (IMD Gridded) and Open-Meteo Historical Archive.
3. **Demographic Registry (`districts_master.csv`):**
   - 38 Tamil Nadu districts.
   - Parameters: Census 2011 population, CAGR projected populations for 2021, 2025, and 2026, population density, urban/rural split, sanitation coverage.
   - Provenance: Census of India & Department of Economics and Statistics, Tamil Nadu.
4. **Disease Surveillance Dataset (`disease_cleaned.csv`):**
   - 2,698 observations covering all 38 districts (2021–2026).
   - Tracked Conditions: Cholera, Typhoid, Acute Diarrhoeal Disease, Hepatitis.
   - Parameters: Reported cases, incidence rate per 100,000 population.
   - Provenance: Integrated Disease Surveillance Programme (IDSP) & IHIP Tamil Nadu.

### 11.2 Ingesting New Surveillance CSVs:
- Headers must match canonical attribute names defined in `DATA_DICTIONARY.md`.
- Values must conform to physical boundaries (e.g. pH between 0.0 and 14.0, rainfall $\ge 0.0$).
- Upload via `data.html`. The backend validates row schemas before staging.

---

# 12. Machine Learning & Predictive Risk Guide

AquaSafe's machine learning architecture adheres to strict epidemiological modeling rigor:

### 12.1 Chronological Split (Zero-Leakage Architecture)
- **Training Set:** 2021 to 2024 (1,824 records).
- **Hold-Out Prospective Test Set:** 2025 to 2026 (874 records).
- Prevents cross-contamination and guarantees models can forecast genuine unobserved future trends.

### 12.2 Model Architecture & Benchmark
| Model | Algorithm | Test Accuracy | F1-Score | ROC-AUC | Outbreak Recall | Production Role |
|---|---|---|---|---|---|---|
| **Champion** | **Gradient Boosting** | **97.94%** | **97.92%** | **0.9981** | **96.4%** | Primary State Risk Classifier |
| **Challenger** | **Random Forest** | 96.80% | 96.72% | 0.9955 | 96.4% | Robust Ensemble Fallback |
| **Baseline** | **Extra Trees** | 91.30% | 90.84% | 0.9799 | 94.2% | Ultra-Fast Edge Baseline |

### 12.3 Scientific Water Quality Index (WQI) Formula
The WQI is computed via a weighted arithmetic index incorporating BIS 10500:2012 safety boundaries:
$$\text{WQI} = 12 \cdot (\text{pH} - 7.0)^2 + 5.5 \cdot \text{Turbidity} + 0.042 \cdot \text{TDS} + 15.0 \cdot \max(0, 5.0 - \text{DO})$$
- $\text{WQI} < 40$: **Good / Safe Drinking Water State**
- $40 \le \text{WQI} < 65$: **Moderate Contamination / Caution State**
- $\text{WQI} \ge 65$: **Severe Hazard / High Outbreak Probability State**

---

# 13. Big Data Processing with Apache Spark

AquaSafe utilizes Apache Spark 4.2.0 and PySpark for large-scale distributed data processing.

### 13.1 Processing Workflow
1. **Distributed DataFrame Ingestion:** Spark reads raw CSVs and infers typed schemas across distributed executor memory partitions.
2. **Schema Sanitization:** Null records are removed and values outside physical domain bounds are dropped.
3. **Distributed Relational Join:** Executes an inner join on `(district_name, observation_date)` across Water Quality, Weather, and Disease surveillance DataFrames, followed by a left join with District Demographics.
4. **Distributed Aggregations:** Computes district rollups, seasonal monsoonal trends, and monthly disease progressions.
5. **Partitioned Parquet Storage:** Serializes analytical results to `data/processed/parquet/tamilnadu_epidemiological_analytics.parquet/` partitioned by `district_name` and `year`.

### 13.2 Partition Hierarchy
```
data/processed/parquet/tamilnadu_epidemiological_analytics.parquet/
├── district_name=Ariyalur/
│   ├── year=2021/*.snappy.parquet
│   └── ...
├── district_name=Chennai/
│   ├── year=2021/*.snappy.parquet
│   └── ...
└── district_name=Virudhunagar/
```

---

# 14. System Troubleshooting & Fault Recovery

### 14.1 Port 8080 Already in Use
*Symptom:* `Web server failed to start. Port 8080 was already in use.`  
*Resolution:*
1. Identify the blocking process:
   ```powershell
   Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess
   ```
2. Terminate the blocking process or launch AquaSafe on an alternate port:
   ```powershell
   mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081" -f backend/pom.xml
   ```

### 14.2 MySQL Inactive / Connection Refused
*Symptom:* `Communications link failure. Connection refused.`  
*Resolution:*
AquaSafe features an automatic fault-tolerant database fallback. If MySQL is unreachable, the application automatically boots using an embedded, file-persisted H2 database (`jdbc:h2:file:./data/aquasafe_db`). No manual configuration is required.

### 14.3 Open-Meteo API Timeout / Rate Limit
*Symptom:* `Live sync warning: Open-Meteo request timed out.`  
*Resolution:*
`DataSyncService.java` implements a 3,000ms timeout with retry backoff. If the external API is temporarily down, the system preserves and serves the latest cached observation timestamp, indicating `Live API Status: OFFLINE (CACHED)`.

### 14.4 PySpark Missing Native Hadoop Libraries on Windows
*Symptom:* `WARN Shell: Did not find winutils.exe`  
*Resolution:*
This is a standard non-blocking Apache Spark warning on Windows. The PySpark processor (`spark_processor.py`) catches this and automatically writes partitioned Parquet files using the native `pyarrow` engine, ensuring zero data loss.

---

# 15. Frequently Asked Questions (FAQ)

### For System Administrators
- **Q: Can I add new districts to the system?**  
  *A:* Yes. Add the district entry to `data/cleaned/districts_master.csv` with its latitude, longitude, and population parameters, then restart the Spring Boot service.
- **Q: How do I backup the database?**  
  *A:* For H2, backup the `data/aquasafe_db.mv.db` file. For MySQL, execute `mysqldump -u root -p aquasafe > backup.sql`.

### For Public Health Analysts
- **Q: Why does the model show higher disease risk during post-monsoon months?**  
  *A:* Tamil Nadu experiences its peak rainfall during the Northeast Monsoon (October–December). Heavy runoff flushes terrestrial pathogens into open water sources, elevating turbidity and driving fecal-oral transmission.
- **Q: Can I export raw forecast data to Excel?**  
  *A:* Yes. Navigate to `reports.html` or `forecast.html` and click **"Export CSV"**.

### For District Health Officers
- **Q: Why am I unable to edit records for Tirunelveli when logged in as `dho`?**  
  *A:* The `dho` account is strictly assigned to **Chennai** district. To protect data sovereignty, cross-district edits are prevented by role-based access control.
- **Q: How quickly do live weather changes update on my dashboard?**  
  *A:* Weather data synchronizes automatically every 5 minutes and pushes to your active dashboard via Server-Sent Events (SSE).

---

# 16. Security, Compliance & Public Health Disclaimer

### 16.1 Access Security Policy
- Passwords must be changed in production from default development credentials.
- All administrative actions (user creation, deletion, model retrain) are logged with audit timestamps.

### 16.2 Medical & Public Health Disclaimer
> **IMPORTANT PUBLIC HEALTH NOTICE:**  
> The **AquaSafe** platform is an epidemiological surveillance, risk stratification, and statistical forecasting decision-support system.  
> Predictions and risk categories generated by this platform are based on environmental indicators, historical outbreak patterns, and mathematical models.  
> **AquaSafe does not provide individual clinical medical diagnoses.**  
> Public health interventions, boil-water notices, chlorine dosing directives, and pharmaceutical deployments must always be verified and authorized by qualified medical officers and the Department of Public Health and Preventive Medicine, Government of Tamil Nadu.

