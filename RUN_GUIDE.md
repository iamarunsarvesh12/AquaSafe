# AquaSafe: Quick Run Guide & Operational Manual

This document provides the single commands to run the **AquaSafe** platform, descriptions of all 14 frontend modules, and operational guidelines.

---

## 🚀 1. The Single Command to Run the Project

Run **ONE** of the following commands from the project root (`c:\Users\Admin\Documents\AquaSafe`):

### Windows PowerShell (Recommended for Windows):
```powershell
.\run.ps1
```

### Windows Command Prompt:
```cmd
run.bat
```

### Python Cross-Platform Launcher (Windows / Linux / macOS):
```bash
python run.py
```

### Linux / macOS:
```bash
chmod +x run.sh
./run.sh
```

---

## ⚡ What That Single Command Automatically Does

The launcher executes the entire platform startup sequence:
1. **Checks Java 17+ and Python 3.11 Runtime**: Validates compiler and execution environments.
2. **Checks & Generates 5-Year Datasets (2021–2026)**: If missing, runs `scripts/generate_tamilnadu_dataset.py` generating 2,698 records per domain across all 38 districts of Tamil Nadu.
3. **Checks & Executes Apache Spark Pipeline**: If missing, runs `spark/spark_processor.py` generating the Snappy-compressed columnar Parquet dataset (`3.35 MB`).
4. **Checks & Trains Machine Learning Models**: If missing, runs `ml/ml_engine.py` evaluating Random Forest, Gradient Boosting, disease-specific models, and 7-day/30-day time-series forecasts.
5. **Compiles & Packages Backend**: Checks for `aquasafe-backend-1.0.0-SNAPSHOT.jar`; if missing, automatically compiles with Maven.
6. **Auto-Configures Dual Database**: Seamlessly switches to embedded H2 (MySQL mode) if MySQL 8.x is inactive on port 3306.
7. **Binds to `http://localhost:8080` & Auto-Launches Browser**: Opens your default browser directly to the dashboard.

---

## 🔑 2. Login Credentials

| Username | Password | Role | Description |
|---|---|---|---|
| `admin` | `admin123` | **ADMIN** | System Administrator (Full configuration, user mgmt, ML retrain, Spark trigger) |
| `analyst` | `user123` | **PUBLIC_HEALTH_ANALYST** | Epidemiologist / Analyst (Surveillance, predictions, forecasts, reports) |
| `dho_chennai` | `officer123` | **DISTRICT_HEALTH_OFFICER** | District Health Officer (Field monitoring, active alerts) |

---

## 🌐 3. All 14 Interactive Web Modules

| # | Module Name | URL | Primary Function |
|---|---|---|---|
| 1 | **Dashboard** | `http://localhost:8080/dashboard.html` | High-level KPIs, 38-district risk distribution, WQI trends |
| 2 | **District Intelligence** | `http://localhost:8080/districts.html` | All 38 districts deep dive, population, area, sanitation ranking |
| 3 | **Geospatial Risk Map** | `http://localhost:8080/map.html` | Leaflet GIS interactive map with disease filters & risk pins |
| 4 | **Risk Prediction Engine** | `http://localhost:8080/prediction.html` | Real-time multi-indicator ML evaluation & disease risk scores |
| 5 | **Disease Forecasting** | `http://localhost:8080/forecast.html` | 7-day & 30-day ahead outbreak projections with 95% CI |
| 6 | **Water Quality Analytics**| `http://localhost:8080/water-quality.html` | BIS 10500:2012 standards, WAWQI scores, threshold violations |
| 7 | **Weather Analytics** | `http://localhost:8080/weather.html` | IMD observations, Northeast/Southwest monsoon analysis, SMAs |
| 8 | **Disease Surveillance** | `http://localhost:8080/disease.html` | Cholera, Typhoid, ADD, Hepatitis case logs & incidence rates |
| 9 | **Big Data & Spark Engine**| `http://localhost:8080/spark.html` | Trigger PySpark job, view partitions, Parquet metrics, execution logs |
| 10 | **Data Management** | `http://localhost:8080/data.html` | Provenance registry, CSV upload, data cleaning & deduplication |
| 11 | **Model Performance** | `http://localhost:8080/model.html` | Algorithm comparison, 10-fold CV, feature importance, confusion matrix |
| 12 | **Surveillance Alerts** | `http://localhost:8080/alerts.html` | Real-time critical outbreak alerts and public health advisories |
| 13 | **Analytical Reports** | `http://localhost:8080/reports.html` | Printable executive summaries & PDF export |
| 14 | **User Management** | `http://localhost:8080/users.html` | Role-based account creation, authorization, and audit logs |

---

## 🔧 4. Command-Line Options for `run.py`

```bash
# Standard 1-command startup:
python run.py

# Recompile Maven package and run:
python run.py --build

# Run Apache Spark PySpark 4.2.0 ETL & Parquet generation:
python run.py --spark

# Run Scikit-Learn training, benchmarking & time-series forecasting:
python run.py --ml

# Regenerate 5-year multi-source raw datasets for all 38 districts:
python run.py --data
```
