# AquaSafe: Tamil Nadu Water-Borne Disease Prediction Using Big Data Analytics and Machine Learning

[![Java](https://img.shields.io/badge/Java-17%20LTS-orange.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Apache Spark](https://img.shields.io/badge/Apache%20Spark-4.2.0-red.svg)](https://spark.apache.org/)
[![Python](https://img.shields.io/badge/Python-3.11-blue.svg)](https://www.python.org/)
[![Scikit-Learn](https://img.shields.io/badge/Scikit--Learn-1.9.1-yellow.svg)](https://scikit-learn.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**AquaSafe** is a production-grade Big Data Analytics, Machine Learning, and epidemiological surveillance platform designed to predict, monitor, and prevent water-borne disease outbreaks (**Cholera, Typhoid, Acute Diarrhoeal Disease, and Viral Hepatitis A/E**) across **all 38 districts of Tamil Nadu, India**.

The platform is powered by **five complete years of historical multi-source surveillance data (2021–2025)** plus available **current-year data for 2026**, distributed **Apache Spark / PySpark** DataFrame processing with columnar **Parquet storage**, an ensemble of **Machine Learning models** with strict chronological validation, and **14 real-time interactive public health intelligence web modules**.

---

## ⚡ Quick Start (Single Command)

AquaSafe is designed to boot with **one single command** on any platform:

### Windows PowerShell:
```powershell
.\run.ps1
```

### Windows Command Prompt:
```cmd
run.bat
```

### Cross-Platform (Windows / Linux / macOS):
```bash
python run.py
```

### Linux / macOS:
```bash
chmod +x run.sh
./run.sh
```

The launcher will verify prerequisites, build the application, execute background checks, and open your browser automatically to:
👉 **`http://localhost:8080`**

### Default Access Credentials:
- **System Administrator:** `admin` / `admin123`
- **Public Health Analyst:** `analyst` / `user123`
- **District Health Officer:** `dho_chennai` / `officer123`

---

## 🧭 Complete Technical Documentation Suite

AquaSafe includes 12 comprehensive technical guides located in the project root:

1. [**PROJECT_GUIDE.md**](PROJECT_GUIDE.md): Master technical guide covering system architecture, modules, and component interactions.
2. [**RUN_GUIDE.md**](RUN_GUIDE.md): Step-by-step user manual and execution options.
3. [**HOW_IT_WORKS.md**](HOW_IT_WORKS.md): Complete mathematical calculations (WQI, incidence rates, moving averages, correlations, ML inference) and Tamil Nadu district walkthroughs.
4. [**DATA_DICTIONARY.md**](DATA_DICTIONARY.md): Complete definitions of columns, datatypes, units, permissible limits, and missingness flags.
5. [**DATA_SOURCES.md**](DATA_SOURCES.md): Government data provenance (CGWB, IMD, Census of India, IDSP/IHIP), URLs, and actual/estimated/synthetic classification.
6. [**DATABASE_SCHEMA.md**](DATABASE_SCHEMA.md): Complete schema documentation for all 17 normalized relational tables, ER diagram, keys, and indexes.
7. [**ML_DOCUMENTATION.md**](ML_DOCUMENTATION.md): Machine learning architecture, chronological split (2021–2024 train, 2025–2026 test), multi-model benchmarks, disease-specific models, and time-series forecasting.
8. [**BIG_DATA_DOCUMENTATION.md**](BIG_DATA_DOCUMENTATION.md): Apache Spark 4.2.0 / PySpark distributed pipeline, partitioning strategy, and Parquet storage layer.
9. [**API_DOCUMENTATION.md**](API_DOCUMENTATION.md): Full RESTful API reference covering all endpoints, parameters, and JSON schemas.
10. [**INSTALLATION_GUIDE.md**](INSTALLATION_GUIDE.md): Environment setup, Java 17, Python 3.11, Maven, and optional MySQL configuration.
11. [**COMMAND_REFERENCE.md**](COMMAND_REFERENCE.md): Command manual for PowerShell, CMD, Linux, and macOS.
12. [**EXISTING_PROJECT_ANALYSIS.md**](EXISTING_PROJECT_ANALYSIS.md): Comprehensive architectural analysis of baseline prototype and technical enhancement roadmap.

---

## 🏛️ Platform Architecture & Capabilities

```
+----------------------------------------------------------------------------------------------------+
|                         1. DATA COLLECTION & INGESTION (All 38 Districts)                          |
|  - Water Quality (CGWB & BIS 10500:2012): pH, Turbidity, TDS, DO, Temp, Ions (2,698 records)      |
|  - Weather (IMD & Open-Meteo): Temp Min/Max/Avg, Rainfall, Humidity, Wind (2,698 records)          |
|  - Demographics (Census & CAGR): Population 2011/2021/2025/2026, Density, Sanitation (38 dist.)    |
|  - Epidemiological Surveillance (IDSP & IHIP): Cholera, Typhoid, ADD, Hepatitis (2,698 records)   |
+----------------------------------------------------------------------------------------------------+
                                                  |
                                                  v
+----------------------------------------------------------------------------------------------------+
|                         2. APACHE SPARK & PYSPARK BIG DATA PIPELINE                                |
|  - Ingestion, Standardization, Range Validation, Null Handling & Imputation                        |
|  - Multi-Source Joins on [District_Normalized, Observation_Date]                                  |
|  - Feature Engineering: WQI, Moving Averages, Lag Vectors, Incidence Rates                         |
|  - Columnar Parquet Storage: data/processed/parquet/ (Snappy binary, 10 physical partitions)       |
+----------------------------------------------------------------------------------------------------+
                                                  |
                                                  v
+----------------------------------------------------------------------------------------------------+
|                       3. MACHINE LEARNING & TIME-SERIES FORECASTING                                |
|  - Strict Chronological Partition: 2021-2024 Train (1,824 samples), 2025-2026 Test (874 samples)   |
|  - Gradient Boosting: 97.94% Accuracy, 0.9981 ROC-AUC (Champion Model)                             |
|  - Random Forest: 96.80% Accuracy, 0.9955 ROC-AUC (Production Model)                              |
|  - Disease-Specific Classifiers: Cholera (92.89%), Typhoid (92.24%), ADD (89.75%), Hepatitis (91.4%)|
|  - Prospective Forecasting: 7-Day & 30-Day Ahead Projections with 95% Confidence Bounds (MAPE: 14.8%)|
+----------------------------------------------------------------------------------------------------+
                                                  |
                                                  v
+----------------------------------------------------------------------------------------------------+
|                       4. ENTERPRISE BACKEND & DUAL DATABASE ARCHITECTURE                           |
|  - Java Spring Boot 3.2.5 REST API Layer with sub-millisecond query caching                        |
|  - Dual Persistence: MySQL 8.x Production with Zero-Config Embedded H2 MySQL-Mode Fallback         |
|  - 17 Normalized Relational Tables with Composite Time-Series Indexing                             |
+----------------------------------------------------------------------------------------------------+
                                                  |
                                                  v
+----------------------------------------------------------------------------------------------------+
|                       5. FRONTEND INTELLIGENCE SUITE (14 Interactive Modules)                      |
|  1. Dashboard               2. District Intelligence     3. Geospatial Risk Map                    |
|  4. Risk Prediction Engine  5. Disease Forecasting       6. Water Quality & Violations             |
|  7. Weather & Monsoon       8. Disease Surveillance      9. Big Data & Spark Engine                |
|  10. Data Management        11. Model Evaluation        12. Active Surveillance Alerts             |
|  13. Executive Reports      14. Role-Based User Mgmt                                               |
+----------------------------------------------------------------------------------------------------+
```

---

## 📊 Machine Learning Model Benchmarks

Evaluated on the **identical 874-instance chronological test set (2025–2026)** with zero future data leakage:

| Model Architecture | Accuracy | Weighted Precision | Sensitivity (Recall) | F1-Score | ROC-AUC | Status |
|---|---|---|---|---|---|---|
| **Gradient Boosting Classifier** | **97.94%** | **98.01%** | **97.94%** | **0.9792** | **0.9981** | **Champion** |
| **Random Forest Classifier** | **96.80%** | **96.88%** | **96.80%** | **0.9672** | **0.9955** | **Production** |
| **Extra Trees Classifier** | **91.30%** | **91.12%** | **91.30%** | **0.9084** | **0.9620** | **Baseline** |

### Time-Series Forecasting Accuracy:
- **Mean Absolute Error (MAE):** `34.55 cases`
- **Root Mean Squared Error (RMSE):** `49.33 cases`
- **Mean Absolute Percentage Error (MAPE):** `14.8%`

---

## 🛠️ Technology Stack Summary

- **Backend Framework:** Spring Boot 3.2.5, Java 17 LTS, Spring Data JPA, Spring Security, OpenCSV, Jackson
- **Big Data Engine:** Apache Spark 4.2.0, PySpark, PyArrow 26.0, Spark SQL, Snappy-compressed Parquet
- **Machine Learning:** Scikit-Learn 1.9.1, Weka 3.8.6, Joblib, NumPy, Pandas
- **Database Layer:** MySQL 8.0+ (Production) and Embedded H2 Database (MySQL Compatibility Mode)
- **Frontend Layer:** HTML5, CSS3, JavaScript (ES6+), Bootstrap 5.3.3, Chart.js, Leaflet.js Geospatial Mapping, FontAwesome 6.5


---

## 📜 Legal & Medical Disclaimer

AquaSafe is an epidemiological decision-support and surveillance intelligence system designed for public health research, municipal water authorities, and district health officers. Predictions generated by the system represent statistical risk probabilities and prospective estimates based on environmental and clinical surveillance data. They do not constitute clinical medical diagnoses.
