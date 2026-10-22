# AquaSafe - Quick Run Guide (Single Command Execution)

This document provides the exact single commands to run the AquaSafe project, the summary of its technical stack, and immediate operational steps.

For full architectural details, equations, and REST endpoint references, consult [PROJECT_GUIDE.md](PROJECT_GUIDE.md).

---

## 🚀 1. The Single Command to Run the Project

Run **ONE** of the following commands from the root `AquaSafe` directory:

### For Windows:
```cmd
.\run.bat
```
*(You can also simply double-click `run.bat` in Windows File Explorer, or run `.\run.ps1` in PowerShell)*

### For Python (All Operating Systems):
```bash
python run.py
```

### For Linux / macOS / Git Bash:
```bash
./run.sh
```

### For Make:
```bash
make run
```

---

## ⚡ What That Single Command Automatically Does

You do **not** need to set up MySQL, configure Apache Tomcat, start a Python backend, or configure node servers. The single command automatically:

1. **Checks Java Environment**: Validates that Java 17+ is installed.
2. **Builds Backend Package**: Checks for `backend/target/aquasafe-backend-1.0.0-SNAPSHOT.jar`; if missing, automatically compiles and packages it.
3. **Auto-Configures Database**: Attempts connection to MySQL on port 3306; if not running, seamlessly switches to embedded in-memory H2 in MySQL compatibility mode with zero configuration.
4. **Seeds Surveillance Datasets**: Loads demographics, water quality time-series, meteorological data, and historical disease records.
5. **Loads & Validates ML Model**: Loads the Weka Random Forest model (100 decision trees) and runs 10-fold cross-validation.
6. **Starts Server**: Binds the application to `http://localhost:8080`.
7. **Launches Default Browser**: Automatically opens `http://localhost:8080` once the server is healthy.

---

## 🛠️ 2. Technical Stack Summary

- **Backend**: Java 17 LTS, Spring Boot 3.2.5 (Web, Data JPA, Security, Validation, OpenCSV).
- **Machine Learning**: Weka 3.8.6 (Random Forest with 100 Trees, 10-Fold Cross-Validation, serialized model format, feature scaling, 10 environmental & demographic inputs).
- **Big Data Engine**: Apache Spark 3.x / PySpark with resilient fallback to high-throughput In-Memory Multi-threaded Streaming Dispatcher.
- **Database**: Dual-engine architecture — Automatic In-Memory H2 in MySQL Compatibility Mode (zero installation needed) + MySQL 8.x Enterprise with HikariCP.
- **Frontend**: HTML5, Modern CSS3 (Glassmorphism, custom design system, CSS Variables), Vanilla JS ES6+, Bootstrap 5.3, Chart.js 4, Leaflet.js (GIS spatial visualization), FontAwesome 6, Google Inter typography.
- **External APIs**: Open-Meteo REST API (free, real-time meteorological conditions).

---

## 🔑 3. Login Credentials

| Username | Password | Role | Description |
| :--- | :--- | :--- | :--- |
| `admin` | `admin123` | **ADMIN** | Full administrative rights (Upload CSVs, Data Preprocessing, ML Retraining, Big Data trigger) |
| `analyst` | `user123` | **USER** | Public Health Analyst (View Dashboard, ML Predictions, GIS Map, Export Reports) |

*(Quick Credential buttons are available on `login.html` for 1-click filling)*

---

## 📋 4. How It Works (Quick Overview)

1. **Data Ingestion**: Multi-source surveillance datasets (water quality, weather, population density, historical disease cases) are loaded into the database.
2. **Data Preprocessing**: Filters sensor anomalies, removes duplicate records, imputes missing values, and assigns deterministic ground-truth risk categories.
3. **Machine Learning Prediction**: When new environmental readings are submitted, the Weka Random Forest classifier evaluates the 10 features and predicts the exact statistical probability and risk category: **HIGH**, **MEDIUM**, or **LOW**.
4. **Geospatial Intelligence**: Leaflet.js plots the districts on an interactive map with risk-colored pins (Red for High, Amber for Medium, Green for Low).
5. **Automated Alerting**: When high risk or contamination is detected, an automated public health warning is generated.
6. **Reporting & PDF Export**: Analysts can filter historical metrics by district and date range and print / export official executive PDF summaries.

---

## 🌐 5. Key URLs

- **Dashboard**: `http://localhost:8080/dashboard.html`
- **Interactive Risk Map**: `http://localhost:8080/map.html`
- **Machine Learning Prediction**: `http://localhost:8080/prediction.html`
- **Data Management & Cleaning**: `http://localhost:8080/data.html`
- **Model Performance & CV**: `http://localhost:8080/model.html`
- **Surveillance Alerts**: `http://localhost:8080/alerts.html`
- **Analytical Reports**: `http://localhost:8080/reports.html`
- **Login Screen**: `http://localhost:8080/login.html`
- **System Health Endpoint**: `http://localhost:8080/api/system/health`
